package com.example.order_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin")
public class AdminHomeController {


    @GetMapping("/home")
    public String adminHome() {
        return "admin_home";
    }
}
