package com.example.order_app.controller;

import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Product;
import com.example.order_app.model.User;
import com.example.order_app.service.cart.ICartItemService;
import com.example.order_app.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartItemController {
    private final ICartItemService cartItemService;
    private final IUserService userService;

    /**
     * Adds an item to the cart.
     *
     * @param cartId ID of the cart
     * @param productId ID of the product to add
     * @param quantity Quantity of the product to add
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect URL
     */
    @PostMapping("/{cartId}/item/add")
    public String addItemToCart(
            @PathVariable Long cartId,
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getAuthenticatedUser();
            if (cartItemService.isCartOwnedByUser(cartId, user.getId())) {
                redirectAttributes.addFlashAttribute("error", "You can only add items to your own cart.");
                return "redirect:/home";
            }

            cartItemService.addItemToCart(cartId, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Item added to cart successfully!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred while adding the item to cart.");
        }
        return "redirect:/cart/" + cartId + "/view";
    }


    /**
     * Updates the quantity of an item in the cart.
     *
     * @param cartId ID of the cart
     * @param itemId ID of the item to update
     * @param quantity New quantity of the item
     * @param action Action to perform (increase, decrease, or update)
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect URL
     */
    @PutMapping("/{cartId}/item/{itemId}/update")
    public String updateItemQuantity(
            @PathVariable Long cartId,
            @PathVariable Long itemId,
            @RequestParam Integer quantity,
            @RequestParam String action,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getAuthenticatedUser();
            if (cartItemService.isCartOwnedByUser(cartId, user.getId())) {
                redirectAttributes.addFlashAttribute("error", "You can only update items in your own cart.");
                return "redirect:/home";
            }

            Product product = cartItemService.getProduct(cartId, itemId);
            switch (action) {
                case "increase":
                    cartItemService.updateItemQuantity(cartId, product.getId(), quantity + 1);
                    break;
                case "decrease":
                    if (quantity > 1) {
                        cartItemService.updateItemQuantity(cartId, product.getId(), quantity - 1);
                    }
                    break;
                case "update":
                    cartItemService.updateItemQuantity(cartId, product.getId(), quantity);
                    break;
                default:
                    redirectAttributes.addFlashAttribute("error", "Invalid action specified.");
                    return "redirect:/cart/" + cartId + "/view";
            }
            redirectAttributes.addFlashAttribute("success", "Cart updated successfully!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred while updating the cart.");
        }
        return "redirect:/cart/" + cartId + "/view";
    }

    /**
     * Removes an item from the cart.
     *
     * @param cartId ID of the cart
     * @param itemId ID of the item to remove
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect URL
     */
    @DeleteMapping("/{cartId}/item/{itemId}/remove")
    public String removeItemFromCart(@PathVariable Long cartId, @PathVariable Long itemId, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getAuthenticatedUser();
            if (cartItemService.isCartOwnedByUser(cartId, user.getId())) {
                redirectAttributes.addFlashAttribute("error", "You can only remove items from your own cart.");
                return "redirect:/home";
            }

            Product product = cartItemService.getProduct(cartId, itemId);
            cartItemService.removeItemFromCart(cartId, product.getId());
            redirectAttributes.addFlashAttribute("success", "Item removed from cart!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred while removing the item from cart.");
        }
        return "redirect:/cart/" + cartId + "/view";
    }
}
