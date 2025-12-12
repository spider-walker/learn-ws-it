package com.emrys.learn.it.web.filters;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, java.io.IOException {

        log.info("➡️ Filter Request: " + request.getMethod() + " " + request.getRequestURI());

        filterChain.doFilter(request, response);

        log.info("⬅️ Filter Response: " + response.getStatus());
    }

}
