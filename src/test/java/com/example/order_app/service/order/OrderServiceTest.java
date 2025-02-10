package com.example.order_app.service.order;

import com.example.order_app.enums.OrderStatus;
import com.example.order_app.model.Cart;
import com.example.order_app.model.Order;
import com.example.order_app.model.User;
import com.example.order_app.repository.OrderRepository;
import com.example.order_app.service.cart.ICartService;
import com.example.order_app.util.TestDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ICartService cartService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Cart testCart;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testUser = TestDataUtil.createTestUser();
        testCart = new Cart();
        testCart.setUser(testUser);
        testCart.setTotalAmount(new BigDecimal("99.99"));

        testOrder = new Order();
        testOrder.setUser(testUser);
        testOrder.setOrderStatus(OrderStatus.PENDING);
        testOrder.setTotalAmount(testCart.getTotalAmount());
    }

    @Test
    void shouldPlaceOrder() {
        when(cartService.getCartByUserId(testUser.getId())).thenReturn(testCart);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order result = orderService.placeOrder(testUser.getId());

        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getOrderStatus());
        assertEquals(testUser, result.getUser());
        verify(cartService).clearCart(any());
    }
}