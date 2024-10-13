package com.example.order_app.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestErrorController {

    @GetMapping("/test-error")
    public String testError() {
        throw new RuntimeException("Test error");
    }

    @GetMapping("/test-access-denied")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String testAccessDenied() {
        return "This should not be visible if access is denied";
    }
}