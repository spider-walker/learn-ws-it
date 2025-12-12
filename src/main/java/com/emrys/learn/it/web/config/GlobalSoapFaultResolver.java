package com.emrys.learn.it.web.config;

import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;
public class GlobalSoapFaultResolver extends SoapFaultMappingExceptionResolver {

    @Override
    protected SoapFaultDefinition getFaultDefinition(Object endpoint, Exception ex) {
        SoapFaultDefinition definition = new SoapFaultDefinition();
        definition.setFaultCode(SoapFaultDefinition.CLIENT);
        return definition;
    }

    @Override
    protected void customizeFault(Object endpoint, Exception ex, SoapFault fault) {
        fault.addFaultDetail()
             .addFaultDetailElement(new javax.xml.namespace.QName("http://example.com/errors", "errorCode"))
             .addText("NO_MAPPING_FOUND");
        fault.addFaultDetail()
             .addFaultDetailElement(new javax.xml.namespace.QName("http://example.com/errors", "message"))
             .addText("No endpoint mapping found for this request. Please check namespace and operation.");
    }
}

