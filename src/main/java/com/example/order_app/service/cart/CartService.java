package com.example.order_app.service.cart;


import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Cart;
import com.example.order_app.model.User;
import com.example.order_app.repository.CartItemRepository;
import com.example.order_app.repository.CartRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService{
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final EntityManager entityManager;


    @Override
    public Cart getCart(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        BigDecimal totalAmount = cart.getTotalAmount();
        cart.setTotalAmount(totalAmount);
        return cartRepository.save(cart);
    }


    @Transactional
    @Override
    public void clearCart(Long id) {
        Cart cart = getCart(id);
        cartItemRepository.deleteAllByCartId(id);
        cart.getItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);
        //cartRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteCart(Long id) {
        Cart cart = getCart(id);
        cartItemRepository.deleteAllByCartId(id);
        cartRepository.delete(cart);
    }

    @Override
    public BigDecimal getTotalPrice(Long id) {
        Cart cart = getCart(id);
        return cart.getTotalAmount();
    }

//    @Override
//    public Cart initializeNewCart(User user) {
//        return Optional.ofNullable(getCartByUserId(user.getId()))
//                .orElseGet(() -> {
//                    Cart cart = new Cart();
//                    cart.setUser(user);
//                    return cartRepository.save(cart);
//                });
//    }
    @Override
    public Cart initializeNewCart(User user) {
        Cart existingCart = getCartByUserId(user.getId());
        if (existingCart != null) {
            return existingCart;
        }
        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setTotalAmount(BigDecimal.ZERO);
        return cartRepository.save(newCart);
    }

    @Override
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

}