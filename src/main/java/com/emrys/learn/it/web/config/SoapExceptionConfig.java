package com.emrys.learn.it.web.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;

import com.emrys.learn.it.web.exceptions.InvalidOperationException;

@Configuration
public class SoapExceptionConfig {

    @Bean
    public CustomSoapFaultResolver customSoapFaultResolver() {
        CustomSoapFaultResolver resolver = new CustomSoapFaultResolver();

        Properties mappings = new Properties();
        mappings.setProperty(InvalidOperationException.class.getName(), SoapFaultDefinition.CLIENT.toString());
        resolver.setExceptionMappings(mappings);

        resolver.setOrder(1);
        return resolver;
    }

    @Bean
    public NoEndpointMappingResolver noEndpointMappingResolver() {
        return new NoEndpointMappingResolver();
    }
}
