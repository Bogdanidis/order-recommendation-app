package com.example.order_app.advice;

import com.example.order_app.model.Cart;
import com.example.order_app.model.User;
import com.example.order_app.service.cart.ICartService;
import com.example.order_app.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;


@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final ICartService cartService;
    private final IUserService userService;


    @ModelAttribute("cart")
    public Cart getUserCart(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            User user = userService.getAuthenticatedUser();
            boolean isAdmin =
                    user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

            if (isAdmin){
                return null;
            }else{
                return cartService.initializeNewCart(user);
            }
        }
        return null;
    }
}