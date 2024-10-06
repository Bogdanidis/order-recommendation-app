package com.example.order_app.controller;


import com.example.order_app.service.category.CategoryService;
import com.example.order_app.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;


@Controller
@RequiredArgsConstructor
public class HomeController {

    private final IProductService productService;
    private final CategoryService categoryService;
    //private final RecommendationService recommendationService;

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        }

        // Load products for the main section
        model.addAttribute("featuredProducts", productService.getAllProducts());
        model.addAttribute("categories", categoryService.getAllCategories());

        // Add recommended products if the user is logged in
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            //model.addAttribute("recommendedProducts", recommendationService.getRecommendedProducts(username));
            model.addAttribute("recommendedProducts", productService.getAllProducts());

        }

        return "home";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        // Add any necessary data for the admin dashboard
        //model.addAttribute("totalUsers", userService.getTotalUsers());
        //model.addAttribute("totalProducts", productService.getTotalProducts());
        //model.addAttribute("todayOrders", orderService.getTodayOrdersCount());
        //model.addAttribute("todayRevenue", orderService.getTodayRevenue());

        return "admin-dashboard";
    }
}
