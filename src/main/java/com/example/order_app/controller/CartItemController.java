package com.example.order_app.controller;

import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.service.cart.ICartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartItemController {
    private final ICartItemService cartItemService;

    @PostMapping("/{cartId}/item/add")
    public String addItemToCart(
            @PathVariable Long cartId,
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            RedirectAttributes redirectAttributes) {
        try {
            cartItemService.addItemToCart(cartId, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Item added to cart successfully!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart/" + cartId + "/view";
    }

    @DeleteMapping("/{cartId}/item/{itemId}/remove")
    public String removeItemFromCart(@PathVariable Long cartId, @PathVariable Long itemId, RedirectAttributes redirectAttributes) {
        try {
            cartItemService.removeItemFromCart(cartId, itemId);
            redirectAttributes.addFlashAttribute("success", "Item removed from cart!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart/" + cartId + "/view";
    }

    @PutMapping("/{cartId}/item/{itemId}/update")
    public String updateItemQuantity(
            @PathVariable Long cartId,
            @PathVariable Long itemId,
            @RequestParam Integer quantity,
            RedirectAttributes redirectAttributes) {
        try {
            cartItemService.updateItemQuantity(cartId, itemId, quantity);
            redirectAttributes.addFlashAttribute("success", "Cart updated successfully!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart/" + cartId + "/view";
    }
}
