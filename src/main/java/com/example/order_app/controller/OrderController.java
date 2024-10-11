package com.example.order_app.controller;

import com.example.order_app.dto.OrderDto;
import com.example.order_app.dto.UserDto;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Order;
import com.example.order_app.model.User;
import com.example.order_app.service.order.IOrderService;
import com.example.order_app.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
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

    /**
     * Displays a list of all orders (admin only).
     *
     * @param page Page number
     * @param size Number of items per page
     * @param model Spring MVC Model
     * @return The name of the order list view
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getAllOrders(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model) {
        Page<OrderDto> orderPage = orderService.getAllOrders(PageRequest.of(page, size));
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("totalItems", orderPage.getTotalElements());
        return "order/list";
    }


    /**
     * Displays details of a specific order.
     *
     * @param orderId ID of the order
     * @param model Spring MVC Model
     * @param redirectAttributes RedirectAttributes for flash messages
     * @param authentication Spring Security Authentication object
     * @return The name of the order details view or a redirect URL
     */
    @GetMapping("/{orderId}")
    public String getOrderById(@PathVariable Long orderId, Model model, RedirectAttributes redirectAttributes, Authentication authentication) {
        try {
            OrderDto order = orderService.getOrder(orderId);
            UserDto user = userService.convertUserToDto(userService.getUserById(order.getUserId()));

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isOwner = authentication.getName().equals(user.getEmail());

            if (!isAdmin && !isOwner) {
                redirectAttributes.addFlashAttribute("error", "You don't have permission to view this order.");
                return "redirect:/orders/user/" + userService.getAuthenticatedUser().getId();
            }

            model.addAttribute("order", order);
            model.addAttribute("user", user);
            return "order/details";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/orders";
        }
    }

    /**
     * Displays orders for a specific user.
     *
     * @param userId ID of the user
     * @param page Page number
     * @param size Number of items per page
     * @param model Spring MVC Model
     * @param redirectAttributes RedirectAttributes for flash messages
     * @param authentication Spring Security Authentication object
     * @return The name of the user orders view or a redirect URL
     */
    @GetMapping("/user/{userId}")
    public String getUserOrders(@PathVariable Long userId,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                Model model,
                                RedirectAttributes redirectAttributes,
                                Authentication authentication) {
        try {
            User currentUser = userService.getAuthenticatedUser();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin && !currentUser.getId().equals(userId)) {
                redirectAttributes.addFlashAttribute("error", "You don't have permission to view these orders.");
                return "redirect:/orders/user/" + currentUser.getId();
            }

            Page<OrderDto> orderPage = orderService.getUserOrdersPaginated(userId, PageRequest.of(page, size));
            User user = userService.getUserById(userId);
            model.addAttribute("orders", orderPage.getContent());
            model.addAttribute("user", user);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", orderPage.getTotalPages());
            model.addAttribute("totalItems", orderPage.getTotalElements());
            return "order/user-orders";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/home";
        }
    }

    /**
     * Handles order creation.
     *
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect URL
     */
    @PostMapping("/create")
    public String createOrder(RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getAuthenticatedUser();
            Order order = orderService.placeOrder(user.getId());
            redirectAttributes.addFlashAttribute("success", "Order placed successfully");
            return "redirect:/orders/" + order.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error occurred: " + e.getMessage());
            User user = userService.getAuthenticatedUser();
            return "redirect:/cart/" + user.getCart().getId() + "/view";
        }
    }

    /**
     * Handles order cancellation.
     *
     * @param orderId ID of the order to cancel
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect URL
     */
    @PutMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.getAuthenticatedUser();
            boolean isAdmin = currentUser.getRoles().stream()
                    .anyMatch(a -> a.getName().equals("ROLE_ADMIN"));
            if (isAdmin) {
                orderService.cancelOrder(orderId);
            } else {
                orderService.cancelOrder(orderId, currentUser.getId());
            }
            redirectAttributes.addFlashAttribute("success", "Order cancelled successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error cancelling order: " + e.getMessage());
        }
        return "redirect:/orders/" + orderId;
    }
}

