package com.example.order_app.service.order;

import com.example.order_app.dto.OrderDto;
import com.example.order_app.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface IOrderService {
    Page<OrderDto> getAllOrders(Pageable pageable);

    Order placeOrder(Long userId);
    OrderDto getOrder(Long orderId);

    List<OrderDto> getUserOrders(Long userId);
    Page<OrderDto> getUserOrdersPaginated(Long userId, Pageable pageable);

    Long countTodaysOrders();
    BigDecimal getTodaysRevenue();

    void cancelOrder(Long orderId, Long userId);
    void cancelOrder(Long orderId);

}
