package com.example.order_app.controller.rest;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Cart;
import com.example.order_app.model.User;
import com.example.order_app.response.RestResponse;
import com.example.order_app.response.PageMetadata;
import com.example.order_app.service.cart.ICartService;
import com.example.order_app.service.recommendation.IRecommendationService;
import com.example.order_app.service.user.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/carts")
@Tag(name = "Cart", description = "Endpoints for managing shopping carts")
public class CartRestController {
    private final ICartService cartService;
    private final IUserService userService;
    private final IRecommendationService recommendationService;

    /**
     * Get cart details with recommended products
     */
    @GetMapping("/{cartId}")
    @Operation(summary = "Get cart details with recommendations")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    public ResponseEntity<RestResponse<?>> getCart(
            @PathVariable Long cartId,
            @RequestParam(defaultValue = "0") int recommendationPage,
            @RequestParam(defaultValue = "3") int recommendationSize) {
        try {
            User user = userService.getAuthenticatedUser();
            Cart cart = cartService.getCartByUserId(user.getId());

            // Verify cart ownership
            if (!cart.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(FORBIDDEN)
                        .body(new RestResponse<>("You can only access your own cart", null));
            }

            // Get recommended products
            Page<ProductDto> recommendations = recommendationService.getRecommendationsForUser(
                    user, PageRequest.of(recommendationPage, recommendationSize));

            // Combine cart and recommendations in response
            var response = new RestResponse<>("Cart retrieved successfully", cart);
            response.setPage(new PageMetadata(recommendations));

            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }


    /**
     * Clear all items from cart
     */
    @DeleteMapping("/{cartId}/clear")
    @Operation(summary = "Clear cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart cleared successfully"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    public ResponseEntity<RestResponse<?>> clearCart(@PathVariable Long cartId) {
        try {
            User user = userService.getAuthenticatedUser();
            Cart cart = cartService.getCartByUserId(user.getId());

            if (!cart.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(FORBIDDEN)
                        .body(new RestResponse<>("You can only clear your own cart", null));
            }

            cartService.clearCart(cartId);
            return ResponseEntity.ok(new RestResponse<>("Cart cleared successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }



    /**
     * Get cart total price
     */
    @GetMapping("/{cartId}/total")
    @Operation(summary = "Get cart total")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Total retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    public ResponseEntity<RestResponse<?>> getCartTotal(@PathVariable Long cartId) {
        try {
            User user = userService.getAuthenticatedUser();
            Cart cart = cartService.getCartByUserId(user.getId());

            if (!cart.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(FORBIDDEN)
                        .body(new RestResponse<>("You can only view your own cart total", null));
            }

            var total = cartService.getTotalPrice(cartId);
            return ResponseEntity.ok(new RestResponse<>("Cart total retrieved successfully", total));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }
}
