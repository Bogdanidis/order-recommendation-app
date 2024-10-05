package com.example.order_app.security.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // Check user roles and redirect accordingly
        String redirectUrl = request.getContextPath();

        // Assuming roles are prefixed with "ROLE_" (e.g., ROLE_ADMIN, ROLE_USER)
        if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            redirectUrl = "/admin/home";  // Redirect to admin home if the user has the role "ADMIN"
        } else if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"))) {
            redirectUrl = "/home";  // Redirect to user home if the user has the role "USER"
        }

        response.sendRedirect(redirectUrl);  // Redirect based on the determined URL
    }
}
