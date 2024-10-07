package com.example.order_app.controller;

import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Cart;
import com.example.order_app.model.User;
import com.example.order_app.service.cart.ICartService;
import com.example.order_app.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final ICartService cartService;
    private final IUserService userService;

    @GetMapping("/{cartId}/view")
    public String getCart(@PathVariable Long cartId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Cart cart = cartService.getCart(cartId);
            User user = userService.getAuthenticatedUser();

            if (!cart.getUser().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "You can only access your own cart.");
                return "redirect:/error";
            }
            BigDecimal totalPrice = cartService.getTotalPrice(cartId);
            model.addAttribute("totalPrice", totalPrice);
            model.addAttribute("cart", cart);
            return "cart/view-cart"; // This is the Thymeleaf template to display cart items
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/error";
        }
    }

    @DeleteMapping("/{cartId}/clear")
    public String clearCart(@PathVariable Long cartId, RedirectAttributes redirectAttributes) {
        try {
            cartService.clearCart(cartId);
            redirectAttributes.addFlashAttribute("success", "Cart cleared successfully!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart/" + cartId + "/view";
    }


}
