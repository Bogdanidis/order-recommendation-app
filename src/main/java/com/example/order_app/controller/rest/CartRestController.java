package com.example.order_app.controller.rest;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Cart;
import com.example.order_app.model.User;
import com.example.order_app.response.ApiResponse;
import com.example.order_app.response.PageMetadata;
import com.example.order_app.service.cart.ICartService;
import com.example.order_app.service.recommendation.IRecommendationService;
import com.example.order_app.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/carts")
public class CartRestController {
    private final ICartService cartService;
    private final IUserService userService;
    private final IRecommendationService recommendationService;

    /**
     * Get cart details with recommended products
     */
    @GetMapping("/{cartId}")
    public ResponseEntity<ApiResponse<?>> getCart(
            @PathVariable Long cartId,
            @RequestParam(defaultValue = "0") int recommendationPage,
            @RequestParam(defaultValue = "3") int recommendationSize) {
        try {
            User user = userService.getAuthenticatedUser();
            Cart cart = cartService.getCartByUserId(user.getId());

            // Verify cart ownership
            if (!cart.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(FORBIDDEN)
                        .body(new ApiResponse<>("You can only access your own cart", null));
            }

            // Get recommended products
            Page<ProductDto> recommendations = recommendationService.getRecommendationsForUser(
                    user, PageRequest.of(recommendationPage, recommendationSize));

            // Combine cart and recommendations in response
            var response = new ApiResponse<>("Cart retrieved successfully", cart);
            response.setPage(new PageMetadata(recommendations));

            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }


    /**
     * Clear all items from cart
     */
    @DeleteMapping("/{cartId}/clear")
    public ResponseEntity<ApiResponse<?>> clearCart(@PathVariable Long cartId) {
        try {
            User user = userService.getAuthenticatedUser();
            Cart cart = cartService.getCartByUserId(user.getId());

            if (!cart.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(FORBIDDEN)
                        .body(new ApiResponse<>("You can only clear your own cart", null));
            }

            cartService.clearCart(cartId);
            return ResponseEntity.ok(new ApiResponse<>("Cart cleared successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }



    /**
     * Get cart total price
     */
    @GetMapping("/{cartId}/total")
    public ResponseEntity<ApiResponse<?>> getCartTotal(@PathVariable Long cartId) {
        try {
            User user = userService.getAuthenticatedUser();
            Cart cart = cartService.getCartByUserId(user.getId());

            if (!cart.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(FORBIDDEN)
                        .body(new ApiResponse<>("You can only view your own cart total", null));
            }

            var total = cartService.getTotalPrice(cartId);
            return ResponseEntity.ok(new ApiResponse<>("Cart total retrieved successfully", total));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }
}
