package com.example.order_app.controller.rest;

import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Cart;
import com.example.order_app.model.Product;
import com.example.order_app.model.User;
import com.example.order_app.response.RestResponse;
import com.example.order_app.service.cart.ICartItemService;
import com.example.order_app.service.cart.ICartService;
import com.example.order_app.service.user.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/cart-items")
@Tag(name = "Cart Items", description = "Endpoints for managing items in shopping cart")
public class CartItemRestController {
    private final ICartItemService cartItemService;
    private final ICartService cartService;
    private final IUserService userService;



    /**
     * Add item to cart
     */
    @PostMapping
    @Operation(summary = "Add item to cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item added successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<RestResponse<?>> addItemToCart(
            @NotNull @RequestParam Long productId,
            @NotNull @Min(1) @RequestParam Integer quantity) {
        try {
            User user = userService.getAuthenticatedUser();
            Cart cart = cartService.initializeNewCart(user);

            cartItemService.addItemToCart(cart.getId(), productId, quantity);
            return ResponseEntity.ok(new RestResponse<>("Item added to cart successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new RestResponse<>("Error adding item to cart: " + e.getMessage(), null));
        }
    }

    /**
     * Remove item from cart
     */
    @DeleteMapping("/{cartId}/items/{itemId}")
    @Operation(summary = "Remove item from cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item removed successfully"),
            @ApiResponse(responseCode = "404", description = "Item not found"),
            @ApiResponse(responseCode = "403", description = "Not authorized to modify this cart")
    })
    public ResponseEntity<RestResponse<?>> removeItemFromCart(
            @PathVariable Long cartId,
            @PathVariable Long itemId) {
        try {
            User user = userService.getAuthenticatedUser();

            if (!cartItemService.isCartOwnedByUser(cartId, user.getId())) {
                return ResponseEntity.status(FORBIDDEN)
                        .body(new RestResponse<>("You can only remove items from your own cart", null));
            }

            Product product = cartItemService.getProduct(cartId, itemId);
            cartItemService.removeItemFromCart(cartId, product.getId());
            return ResponseEntity.ok(new RestResponse<>("Item removed from cart successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Update item quantity
     */
    @PutMapping("/{cartId}/items/{itemId}")
    @Operation(summary = "Update item quantity")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quantity updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid quantity"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    public ResponseEntity<RestResponse<?>> updateItemQuantity(
            @PathVariable Long cartId,
            @PathVariable Long itemId,
            @NotNull @Min(1) @RequestParam Integer quantity,
            @RequestParam(required = false) String action) {
        try {
            User user = userService.getAuthenticatedUser();

            if (!cartItemService.isCartOwnedByUser(cartId, user.getId())) {
                return ResponseEntity.status(FORBIDDEN)
                        .body(new RestResponse<>("You can only update items in your own cart", null));
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
                                .body(new RestResponse<>("Invalid action specified", null));
                    }
                }
            }

            cartItemService.updateItemQuantity(cartId, product.getId(), newQuantity);
            return ResponseEntity.ok(new RestResponse<>("Item quantity updated successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }

}
