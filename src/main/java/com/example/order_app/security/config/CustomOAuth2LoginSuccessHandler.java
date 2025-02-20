package com.example.order_app.security.config;

import com.example.order_app.security.jwt.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomOAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        if (request.getRequestURI().contains("/api/")) {
            handleApiSuccess(response, authentication);
        } else {
            handleWebSuccess(request, response, authentication);
        }
    }

    private void handleApiSuccess(HttpServletResponse response, Authentication authentication) throws IOException {
        String jwt = jwtUtils.generateTokenForUser(authentication);
        response.setContentType("application/json");
        response.getWriter().write("{\"token\":\"" + jwt + "\"}");
    }

    private void handleWebSuccess(HttpServletRequest request, HttpServletResponse response,
                                  Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);
        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        return isAdmin ? "/admin/dashboard" : "/home";
    }
}