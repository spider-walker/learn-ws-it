package com.emrys.learn.it.web.config;

import javax.xml.namespace.QName;

import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;

import com.emrys.learn.it.web.exceptions.InvalidOperationException;

public class CustomSoapFaultResolver extends SoapFaultMappingExceptionResolver {

    @Override
    protected SoapFaultDefinition getFaultDefinition(Object endpoint, Exception ex) {
        SoapFaultDefinition definition = new SoapFaultDefinition();
        if (ex instanceof InvalidOperationException) {
            definition.setFaultCode(SoapFaultDefinition.CLIENT);
        } else {
            definition.setFaultCode(SoapFaultDefinition.SERVER);
        }
        return definition;
    }

    @Override
    protected void customizeFault(Object endpoint, Exception ex, SoapFault fault) {
        if (ex instanceof InvalidOperationException) {
            InvalidOperationException ioe = (InvalidOperationException) ex;
            // Add custom detail element
            fault.addFaultDetail()
                 .addFaultDetailElement(new QName("http://example.com/errors", "errorCode"))
                 .addText(ioe.getErrorCode());
        }
    }
}
