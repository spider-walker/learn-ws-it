package com.emrys.learn.it.web.filters;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Filter that checks the SOAP body root element (namespace + localName).
 * If the QName is not in allowedOperations, it writes a SOAP Fault and returns.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class SoapOperationValidationFilter implements Filter {

    private final SaajSoapMessageFactory messageFactory;
    private final Set<QName> allowedOperations;

    public SoapOperationValidationFilter(SaajSoapMessageFactory messageFactory) {
        this.messageFactory = messageFactory;
        // Populate allowed operations from your XSD/WSDL or endpoints.
        Set<QName> ops = new HashSet<>();
        ops.add(new QName("http://example.com/order", "getOrderRequestOP"));
        // add other allowed QNames here
        this.allowedOperations = Collections.unmodifiableSet(ops);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        log.info("SoapOperationValidationFilter: Checking SOAP operation for request URI: {}", request.getRequestURI());


        // Only inspect POSTs to the SOAP servlet path
        if (!"POST".equalsIgnoreCase(request.getMethod()) || !request.getRequestURI().contains("/soap")) {
            chain.doFilter(req, res);
            return;
        }

        // Read the request body stream once (do not consume it permanently)
        // We use the request InputStream directly; if other filters/controllers need
        // it,
        // wrap the request with a caching wrapper (ContentCachingRequestWrapper) in
        // production.
        try (InputStream is = request.getInputStream()) {
            QName root = readSoapBodyRootElement(is);
            if (root == null) {
                // malformed or empty body — return a SOAP Fault
                writeSoapFault(response, "Malformed SOAP message or empty body", "MALFORMED_SOAP");
                return;
            }

            if (!allowedOperations.contains(root)) {
                writeSoapFault(response,
                        "Invalid operation: no endpoint mapping found for " + root.getLocalPart(),
                        "NO_MAPPING_FOUND");
                return;
            }

            // If allowed, we must forward the original request body downstream.
            // Because we consumed the InputStream above, wrap the request with a cached
            // copy.
            // For brevity here, we re-create a wrapper that holds the bytes and pass it on.
            CachedBodyHttpServletRequest wrapped = new CachedBodyHttpServletRequest(request, root);
            chain.doFilter(wrapped, response);
        } catch (Exception e) {
            // On unexpected errors, return a server fault
            writeSoapFault(response, "Server error while validating operation", "SERVER_ERROR");
        }
    }

    private QName readSoapBodyRootElement(InputStream is) throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(is);

        // Advance to the Body element, then to the first element inside Body
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                String local = reader.getLocalName();
                String ns = reader.getNamespaceURI() == null ? "" : reader.getNamespaceURI();

                // Look for SOAP Body start (SOAP 1.1 or 1.2)
                if ("Body".equals(local) && (ns.equals("http://schemas.xmlsoap.org/soap/envelope/") ||
                        ns.equals("http://www.w3.org/2003/05/soap-envelope"))) {
                    // Move to the first element inside Body
                    while (reader.hasNext()) {
                        int inner = reader.next();
                        if (inner == XMLStreamConstants.START_ELEMENT) {
                            return new QName(reader.getNamespaceURI(), reader.getLocalName());
                        }
                    }
                }
            }
        }
        return null;
    }

    private void writeSoapFault(HttpServletResponse response, String message, String code) throws IOException {
        try {
            SaajSoapMessage soapResponse = (SaajSoapMessage) messageFactory.createWebServiceMessage();
            SoapBody body = soapResponse.getSoapBody();
            SoapFault fault = body.addClientOrSenderFault(message, null);
            fault.addFaultDetail()
                    .addFaultDetailElement(new QName("http://example.com/errors", "errorCode"))
                    .addText(code);

            response.setStatus(HttpServletResponse.SC_OK); // SOAP 1.1 faults usually 500
            response.setContentType("text/xml;charset=UTF-8");
            soapResponse.writeTo(response.getOutputStream());
        } catch (Exception ex) {
            // If building the SOAP message fails, fallback to plain text
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
        }
    }
}
