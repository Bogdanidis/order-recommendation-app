package com.example.order_app.advice;

import com.example.order_app.model.Cart;
import com.example.order_app.service.cart.ICartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;


@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final ICartService cartService;


    @ModelAttribute("cart")
    public Cart getUserCart(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            return cartService.getCartByUserEmail(userDetails.getUsername());
        }
        return null;
    }
}