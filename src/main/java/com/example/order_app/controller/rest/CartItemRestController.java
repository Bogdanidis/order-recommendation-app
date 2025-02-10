package com.example.order_app.controller.rest;

import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Cart;
import com.example.order_app.model.Product;
import com.example.order_app.model.User;
import com.example.order_app.response.ApiResponse;
import com.example.order_app.service.cart.ICartItemService;
import com.example.order_app.service.cart.ICartService;
import com.example.order_app.service.user.IUserService;
import io.jsonwebtoken.JwtException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/cart-items")
public class CartItemRestController {
    private final ICartItemService cartItemService;
    private final ICartService cartService;
    private final IUserService userService;



    /**
     * Add item to cart
     */
    @PostMapping
    public ResponseEntity<ApiResponse<?>> addItemToCart(
            @NotNull @RequestParam Long productId,
            @NotNull @Min(1) @RequestParam Integer quantity) {
        try {
            User user = userService.getAuthenticatedUser();
            Cart cart = cartService.initializeNewCart(user);

            cartItemService.addItemToCart(cart.getId(), productId, quantity);
            return ResponseEntity.ok(new ApiResponse<>("Item added to cart successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Error adding item to cart: " + e.getMessage(), null));
        }
    }

    /**
     * Remove item from cart
     */
    @DeleteMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<ApiResponse<?>> removeItemFromCart(
            @PathVariable Long cartId,
            @PathVariable Long itemId) {
        try {
            User user = userService.getAuthenticatedUser();

            if (!cartItemService.isCartOwnedByUser(cartId, user.getId())) {
                return ResponseEntity.status(FORBIDDEN)
                        .body(new ApiResponse<>("You can only remove items from your own cart", null));
            }

            Product product = cartItemService.getProduct(cartId, itemId);
            cartItemService.removeItemFromCart(cartId, product.getId());
            return ResponseEntity.ok(new ApiResponse<>("Item removed from cart successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Update item quantity
     */
    @PutMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<ApiResponse<?>> updateItemQuantity(
            @PathVariable Long cartId,
            @PathVariable Long itemId,
            @NotNull @Min(1) @RequestParam Integer quantity,
            @RequestParam(required = false) String action) {
        try {
            User user = userService.getAuthenticatedUser();

            if (!cartItemService.isCartOwnedByUser(cartId, user.getId())) {
                return ResponseEntity.status(FORBIDDEN)
                        .body(new ApiResponse<>("You can only update items in your own cart", null));
            }

            Product product = cartItemService.getProduct(cartId, itemId);
            int newQuantity = quantity;

            // Handle quantity adjustment actions
            if (action != null) {
                switch (action) {
                    case "increase" -> newQuantity = quantity + 1;
                    case "decrease" -> newQuantity = quantity > 1 ? quantity - 1 : 1;
                    case "update" -> newQuantity = quantity;
                    default -> {
                        return ResponseEntity.badRequest()
                                .body(new ApiResponse<>("Invalid action specified", null));
                    }
                }
            }

            cartItemService.updateItemQuantity(cartId, product.getId(), newQuantity);
            return ResponseEntity.ok(new ApiResponse<>("Item quantity updated successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

}
