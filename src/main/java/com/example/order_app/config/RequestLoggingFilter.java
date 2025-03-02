package com.example.order_app.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Generate request ID
        String requestId = UUID.randomUUID().toString();

        // Log request
        logger.info("REQUEST [{}] {} {}", requestId, httpRequest.getMethod(), httpRequest.getRequestURI());

        // Continue with the request
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("REQUEST [{}] failed: {}", requestId, e.getMessage());
            throw e;
        }

        // Log response
        logger.info("RESPONSE [{}] status: {}", requestId, httpResponse.getStatus());
    }
}
