package com.example.order_app.controller;

import com.example.order_app.dto.UserDto;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Cart;
import com.example.order_app.model.User;
import com.example.order_app.service.cart.ICartService;
import com.example.order_app.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final ICartService cartService;
    private final IUserService userService;

    /**
     * Displays the cart view for a specific user.
     *
     * @param cartId ID of the cart
     * @param model Spring MVC Model
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return The name of the cart view or a redirect URL
     */
    @GetMapping("/{cartId}/view")
    public String getCart(@PathVariable Long cartId, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getAuthenticatedUser();
            Cart cart = cartService.getCartByUserId(user.getId());
            UserDto userDto = userService.convertUserToDto(user);

            if (!cart.getUser().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "You can only access your own cart.");
                return "redirect:/home";
            }

            BigDecimal totalPrice = cartService.getTotalPrice(cartId);
            model.addAttribute("totalPrice", totalPrice);
            model.addAttribute("cart", cart);
            model.addAttribute("user", userDto);
            return "cart/view";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/home";
        }
    }

    /**
     * Clears all items from the cart.
     *
     * @param cartId ID of the cart
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect URL
     */
    @PostMapping("/{cartId}/clear")
    public String clearCart(@PathVariable Long cartId, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getAuthenticatedUser();
            Cart cart = cartService.getCartByUserId(user.getId());

            if (!cart.getUser().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "You can only clear your own cart.");
                return "redirect:/home";
            }

            cartService.clearCart(cartId);
            redirectAttributes.addFlashAttribute("success", "Cart cleared successfully!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred while clearing the cart.");
        }
        return "redirect:/cart/" + cartId + "/view";
    }
}
