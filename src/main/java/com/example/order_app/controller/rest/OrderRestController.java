package com.example.order_app.controller.rest;

import com.example.order_app.dto.OrderDto;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Order;
import com.example.order_app.model.User;
import com.example.order_app.response.ApiResponse;
import com.example.order_app.service.order.IOrderService;
import com.example.order_app.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/orders")
public class OrderRestController {
    private final IOrderService orderService;
    private final IUserService userService;

    /**
     * Get all orders (Admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<OrderDto>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<OrderDto> orderPage = orderService.getAllOrders(PageRequest.of(page, size));
        return ResponseEntity.ok(new ApiResponse<>("Orders retrieved successfully", orderPage));
    }

    /**
     * Get specific order details
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderById(@PathVariable Long orderId) {
        try {
            OrderDto order = orderService.getOrder(orderId);

            // Verify access rights
            User currentUser = userService.getAuthenticatedUser();
            boolean isAdmin = currentUser.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
            boolean isOwner = order.getUserId().equals(currentUser.getId());

            if (!isAdmin && !isOwner) {
                return ResponseEntity.status(FORBIDDEN)
                        .body(new ApiResponse<>("You don't have permission to view this order", null));
            }

            return ResponseEntity.ok(new ApiResponse<>("Order found", order));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Get orders for a specific user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<OrderDto>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            User currentUser = userService.getAuthenticatedUser();
            boolean isAdmin = currentUser.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

            if (!isAdmin && !currentUser.getId().equals(userId)) {
                return ResponseEntity.status(FORBIDDEN)
                        .body(new ApiResponse<>("You can only view your own orders", null));
            }

            Page<OrderDto> orderPage = orderService.getUserOrdersPaginated(
                    userId, PageRequest.of(page, size));
            return ResponseEntity.ok(new ApiResponse<>("Orders retrieved successfully", orderPage));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Create new order
     */
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createOrder() {
        try {
            User user = userService.getAuthenticatedUser();
            Order order = orderService.placeOrder(user.getId());
            return ResponseEntity.ok(new ApiResponse<>("Order placed successfully", order));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Error occurred while placing order: " + e.getMessage(), null));
        }
    }


    /**
     * Cancel order
     */
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<?>> cancelOrder(@PathVariable Long orderId) {
        try {
            User currentUser = userService.getAuthenticatedUser();
            boolean isAdmin = currentUser.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

            if (isAdmin) {
                orderService.cancelOrder(orderId);
            } else {
                orderService.cancelOrder(orderId, currentUser.getId());
            }

            return ResponseEntity.ok(new ApiResponse<>("Order cancelled successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Error cancelling order: " + e.getMessage(), null));
        }
    }
}