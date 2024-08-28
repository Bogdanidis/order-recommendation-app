package com.example.order_app.controller;

import com.example.order_app.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/default")
    public String defaultAfterLogin(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/home";
        } else if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"))) {
            return "redirect:/customer/home";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/admin/home")
    public String adminHome() {
        return "admin_home";
    }

    @GetMapping("/customer/home")
    public String userHome() {
        return "customer_home";
    }


}
