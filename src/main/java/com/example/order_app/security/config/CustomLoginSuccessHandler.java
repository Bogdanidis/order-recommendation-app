package com.example.order_app.security.config;

import com.example.order_app.model.User;
import com.example.order_app.security.user.ShopUserDetails;
import com.example.order_app.service.cart.ICartService;
import com.example.order_app.service.user.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // Store user details in the session
        ShopUserDetails userDetails = (ShopUserDetails) authentication.getPrincipal();
        request.getSession().setAttribute("USER_DETAILS", userDetails);

        // Determine redirect URL based on user role
        String redirectUrl = determineTargetUrl(authentication);

        // Redirect the user
        response.sendRedirect(request.getContextPath() + redirectUrl);
    }

    private String determineTargetUrl(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        boolean isUser = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));

        if (isAdmin) {
            return "/admin/dashboard";
        } else if (isUser) {
            return "/home";
        } else {
            return "/";  // Default redirect if no specific role matched
        }
    }
}
