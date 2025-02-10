package com.example.order_app.service.cart;

import com.example.order_app.model.Cart;
import com.example.order_app.model.User;
import com.example.order_app.repository.CartRepository;
import com.example.order_app.util.TestDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    private Cart testCart;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = TestDataUtil.createTestUser();
        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testCart.setTotalAmount(BigDecimal.ZERO);
    }

    @Test
    void shouldInitializeNewCart() {
        when(cartRepository.findByUserId(testUser.getId())).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        Cart result = cartService.initializeNewCart(testUser);

        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void shouldReturnExistingCart() {
        when(cartRepository.findByUserId(testUser.getId())).thenReturn(testCart);

        Cart result = cartService.initializeNewCart(testUser);

        assertNotNull(result);
        assertEquals(testCart.getId(), result.getId());
        verify(cartRepository, never()).save(any(Cart.class));
    }
}