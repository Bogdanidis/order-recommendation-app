package com.example.order_app.controller.rest;

import com.example.order_app.model.Order;
import com.example.order_app.model.User;
import com.example.order_app.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Display the orders for the logged-in customer
    @GetMapping("/customer/my-orders")
    public String viewMyOrders(@AuthenticationPrincipal User currentUser, Model model) {
        List<Order> orders = orderService.getOrdersByCustomerId(currentUser.getId());
        model.addAttribute("orders", orders);
        return "Order/my_orders";
    }
}
