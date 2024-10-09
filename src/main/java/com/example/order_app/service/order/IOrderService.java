package com.example.order_app.service.order;

import com.example.order_app.dto.OrderDto;
import com.example.order_app.model.Order;

import java.math.BigDecimal;
import java.util.List;

public interface IOrderService {
    Order placeOrder(Long userId);
    OrderDto getOrder(Long orderId);
    List<OrderDto> getUserOrders(Long userId);

    Long countTodaysOrders();
    BigDecimal getTodaysRevenue();

    void cancelOrder(Long orderId, Long userId);
}
