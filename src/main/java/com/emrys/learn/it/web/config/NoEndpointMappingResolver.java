package com.emrys.learn.it.web.config;

import javax.xml.namespace.QName;

import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointExceptionResolver;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapMessage;

public class NoEndpointMappingResolver implements EndpointExceptionResolver {

    @Override
    public boolean resolveException(MessageContext messageContext, Object endpoint, Exception ex) {
        if (ex.getMessage() != null && ex.getMessage().contains("No endpoint mapping found")) {
            SoapMessage response = (SoapMessage) messageContext.getResponse();
            SoapBody body = response.getSoapBody();

            SoapFault fault = body.addClientOrSenderFault("No endpoint mapping found for this request.", null);

            fault.addFaultDetail()
                 .addFaultDetailElement(new QName("http://example.com/errors", "errorCode"))
                 .addText("INVALID_OPERATION");

            return true; // handled
        }
        return false; // let other resolvers handle
    }
}

