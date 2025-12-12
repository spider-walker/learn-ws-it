package com.emrys.learn.it.web.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.emrys.learn.it.web.filters.LoggingFilter;

@Configuration
public class FilterConfig {
    
  
    @Bean
    public FilterRegistrationBean<LoggingFilter> loggingFilter() {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoggingFilter());
        registrationBean.addUrlPatterns("/v1/api/leant-it-ws/*"); // Adjust the URL pattern as needed
        return registrationBean;
    }  
}
