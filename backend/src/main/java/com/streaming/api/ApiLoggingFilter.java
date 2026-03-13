package com.streaming.api;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApiLoggingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(ApiLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest req && response instanceof HttpServletResponse res) {
            if (req.getDispatcherType() == DispatcherType.REQUEST) {
                logger.info("API Request: {} {}", req.getMethod(), req.getRequestURI());
                chain.doFilter(request, response);
                logger.info("API Response: {} {} - Status: {}", req.getMethod(), req.getRequestURI(), res.getStatus());
            } else {
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
