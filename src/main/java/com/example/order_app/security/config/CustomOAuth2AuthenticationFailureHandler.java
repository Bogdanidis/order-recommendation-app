package com.example.order_app.security.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class CustomOAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2AuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String targetUrl = "/auth/login";

        if (request.getRequestURI().contains("/api/")) {
            // Handle API authentication failure
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Authentication failed: " + exception.getMessage() + "\"}");
            return;
        }

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", "OAuth2 authentication failed: " + exception.getMessage())
                .build().toUriString();

        logger.error("OAuth2 authentication failed", exception);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
