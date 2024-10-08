package com.example.order_app.security.config;

import com.example.order_app.model.Cart;
import com.example.order_app.service.user.IUserService;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.order_app.service.cart.ICartService;
import com.example.order_app.security.user.ShopUserDetails;

@Component
public class CustomSessionListener implements HttpSessionListener {

    @Autowired
    private  ICartService cartService;

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // We'll initialize the cart when the user logs in, not here
        // because at this point, the user might not be authenticated yet
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        ShopUserDetails userDetails = (ShopUserDetails) se.getSession().getAttribute("USER_DETAILS");
        if (userDetails != null) {
            Long userId = userDetails.getId();
            Cart cart = cartService.getCartByUserId(userId);
            if (cart != null) {
                cartService.deleteCart(cart.getId());
            }
        }
    }
}