package com.example.order_app.controller;

import com.example.order_app.model.Order;
import com.example.order_app.model.Product;
import com.example.order_app.model.User;
import com.example.order_app.service.OrderService;
import com.example.order_app.service.ProductService;
import com.example.order_app.service.RecommendationService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("customer")
@AllArgsConstructor

public class CustomerHomeController {

    private final ProductService productService;
    private final OrderService orderService;
    private final RecommendationService recommendationService;


    @GetMapping("/home")
    public String userHome() {
        return "customer_home";
    }

    // Display all products
    @GetMapping("/browse-products")
    public String browseProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "Product/browse_products";
    }

    // Display the orders for the logged-in customer
    @GetMapping("/my-orders")
    public String viewMyOrders(@AuthenticationPrincipal User currentUser, Model model) {
        List<Order> orders = orderService.getOrdersByCustomerId(currentUser.getId());
        model.addAttribute("orders", orders);
        return "Order/my_orders";
    }


    @GetMapping("/my-recommendations")
    public String showRecommendations(Model model) {
        List<Product> recommendedProducts = recommendationService.getRecommendedProducts(); // Fetch recommended products
        model.addAttribute("recommendedProducts", recommendedProducts);
        return "Recommendation/my_recommendations";
    }
}
