package com.example.order_app.service.cart;

import com.example.order_app.model.Cart;
import com.example.order_app.model.User;

import java.math.BigDecimal;

public interface ICartService {
    Cart getCart(Long id);
    void emptyCart(Long id);
    void deleteCart(Long id);
    BigDecimal getTotalPrice(Long id);

    Cart initializeNewCart(User user);

    Cart getCartByUserId(Long userId);
    Cart getCartByUserEmail(String email);
}
