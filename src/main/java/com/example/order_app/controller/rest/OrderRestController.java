package com.example.order_app.controller.rest;

import com.example.order_app.dto.OrderDto;
import com.example.order_app.exception.OrderCancellationException;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Order;
import com.example.order_app.model.User;
import com.example.order_app.repository.OrderRepository;
import com.example.order_app.response.RestResponse;
import com.example.order_app.service.order.IOrderService;
import com.example.order_app.service.user.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/orders")
@Tag(name = "Orders", description = "Endpoints for managing orders")
public class OrderRestController {
    private final IOrderService orderService;
    private final IUserService userService;
    private final OrderRepository orderRepository;

    /**
     * Get all orders (Admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all orders", description = "Admin only")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    })
    public ResponseEntity<RestResponse<OrderDto>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<OrderDto> orderPage = orderService.getAllOrders(PageRequest.of(page, size));
        return ResponseEntity.ok(new RestResponse<>("Orders retrieved successfully", orderPage));
    }

    /**
     * Get specific order details
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<RestResponse<OrderDto>> getOrderById(@PathVariable Long orderId) {
        try {
            OrderDto order = orderService.getOrder(orderId);

            // Verify access rights
            User currentUser = userService.getAuthenticatedUser();
            boolean isAdmin = currentUser.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
            boolean isOwner = order.getUserId().equals(currentUser.getId());

            if (!isAdmin && !isOwner) {
                return ResponseEntity.status(FORBIDDEN)
                        .body(new RestResponse<>("You don't have permission to view this order", null));
            }

            return ResponseEntity.ok(new RestResponse<>("Order found", order));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Get orders for a specific user
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user orders")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Not authorized to view these orders")
    })
    public ResponseEntity<RestResponse<OrderDto>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            User currentUser = userService.getAuthenticatedUser();
            boolean isAdmin = currentUser.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

            if (!isAdmin && !currentUser.getId().equals(userId)) {
                return ResponseEntity.status(FORBIDDEN)
                        .body(new RestResponse<>("You can only view your own orders", null));
            }

            Page<OrderDto> orderPage = orderService.getUserOrdersPaginated(
                    userId, PageRequest.of(page, size));
            return ResponseEntity.ok(new RestResponse<>("Orders retrieved successfully", orderPage));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Create new order
     */
    @PostMapping
    @Operation(summary = "Create new order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<RestResponse<?>> createOrder() {
        try {
            User user = userService.getAuthenticatedUser();
            Order order = orderService.placeOrder(user.getId());
            return ResponseEntity.ok(new RestResponse<>("Order placed successfully", order));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new RestResponse<>("Error occurred while placing order: " + e.getMessage(), null));
        }
    }


    /**
     * Cancel order
     */
    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled")
    })
    public ResponseEntity<RestResponse<?>> cancelOrder(@PathVariable Long orderId) {
        try {
            User currentUser = userService.getAuthenticatedUser();
            boolean isAdmin = currentUser.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

            if (isAdmin) {
                orderService.cancelOrder(orderId);
            } else {
               if(Objects.equals(currentUser.getId(), orderService.getOrder(orderId).getUserId())) {
                   orderService.cancelOrder(orderId);
               }else throw new OrderCancellationException("Order does not belong to User");
            }

            return ResponseEntity.ok(new RestResponse<>("Order cancelled successfully", null));
        } catch (ResourceNotFoundException | OrderCancellationException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new RestResponse<>("Error cancelling order: " + e.getMessage(), null));
        }
    }

    /**
     * Delete order
     */
    @DeleteMapping("/{orderId}")
    @Operation(summary = "Delete order")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
    })
    public ResponseEntity<RestResponse<?>> deleteOrder(@PathVariable Long orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok(new RestResponse<>("Order deactivated successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }
}