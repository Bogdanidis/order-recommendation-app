package com.example.order_app.service.cart;

import com.example.order_app.model.CartItem;
import com.example.order_app.model.Product;

public interface ICartItemService {
    void addItemToCart(Long cartId, Long productId, int quantity);
    void removeItemFromCart(Long cartId, Long productId);
    void updateItemQuantity(Long cartId, Long productId, int quantity);

    boolean isCartOwnedByUser(Long cartId, Long userId);

    CartItem getCartItem(Long cartId, Long productId);
    Product getProduct(Long cartId, Long itemId);
}