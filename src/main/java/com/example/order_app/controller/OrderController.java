package com.example.order_app.controller;

import com.example.order_app.dto.OrderDto;
import com.example.order_app.dto.UserDto;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Order;
import com.example.order_app.model.User;
import com.example.order_app.service.order.IOrderService;
import com.example.order_app.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final IOrderService orderService;
    private final IUserService userService;


    @PostMapping("/create")
    public String createOrder(@RequestParam Long userId, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.placeOrder(userId);
            redirectAttributes.addFlashAttribute("message", "Order placed successfully");
            return "redirect:/orders/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error occurred: " + e.getMessage());
            User user = userService.getAuthenticatedUser();
            return "redirect:/cart/" + user.getCart().getId() + "/view";
        }
    }

    @GetMapping("/{orderId}")
    public String getOrderById(@PathVariable Long orderId, Model model, RedirectAttributes redirectAttributes) {
        try {
            OrderDto order = orderService.getOrder(orderId);
            model.addAttribute("order", order);
            UserDto user= userService.convertUserToDto(userService.getAuthenticatedUser());
            model.addAttribute("user", user);
            return "order/details";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/orders/list";
        }
    }

    @GetMapping("/user/{userId}")
    public String getUserOrders(@PathVariable Long userId, Model model, RedirectAttributes redirectAttributes) {
        try {
            List<OrderDto> orders = orderService.getUserOrders(userId);
            User user = userService.getUserById(userId);
            model.addAttribute("orders", orders);
            model.addAttribute("user", user);
            return "order/user-orders";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/orders/list";
        }
    }

    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getAuthenticatedUser();
            orderService.cancelOrder(orderId, user.getId());
            redirectAttributes.addFlashAttribute("message", "Order cancelled successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error cancelling order: " + e.getMessage());
        }
        return "redirect:/orders/user/" + userService.getAuthenticatedUser().getId();
    }
}
