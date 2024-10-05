package com.example.order_app.controller;

import com.example.order_app.request.CreateUserRequest;
import com.example.order_app.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final IUserService userService;

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password. Please try again.");
        }
        return "auth/login"; // Returns the login.html page
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        RedirectAttributes redirectAttributes) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(email, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/home"; // Redirect to home page on successful login
        } catch (AuthenticationException e) {
            redirectAttributes.addAttribute("error", "Invalid credentials");
            return "redirect:/auth/login?error=true"; // Redirect back to login with an error
        }
    }

    @GetMapping("/logout")
    public String logout() {
        SecurityContextHolder.clearContext();
        return "redirect:/auth/login?logout=true"; // Redirect to login with a logout message
    }

    // New: Registration Form
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new CreateUserRequest());
        return "auth/register";
    }

    // New: Handle Registration Form Submission
    @PostMapping("/register")
    public String register(@ModelAttribute("user") CreateUserRequest registerRequest,
                           RedirectAttributes redirectAttributes) {
        try {
            userService.createUser(registerRequest);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please log in.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/register";
        }
    }
}

