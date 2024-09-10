package com.example.order_app.service;

import com.example.order_app.model.Order;
import com.example.order_app.model.OrderProduct;
import com.example.order_app.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    /*
    // Calculate total amount of an order
    public double calculateTotalAmount(Order order) {
        return order.getOrderProducts().stream()
                .mapToDouble(orderProduct -> orderProduct.getProduct().getPrice() * orderProduct.getQuantity())
                .sum();
    }
     */

}
