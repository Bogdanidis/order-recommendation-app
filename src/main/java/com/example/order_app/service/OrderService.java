package com.example.order_app.service;

import com.example.order_app.model.Order;
import com.example.order_app.model.OrderProduct;
import com.example.order_app.model.Product;
import com.example.order_app.repository.OrderRepository;
import com.example.order_app.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public void cancelOrder(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            if ("Pending".equals(order.getStatus())) {  // Check if the order is in a cancellable state
                order.setStatus("Cancelled");

                Set<OrderProduct> orderProducts = order.getOrderProducts();
                for (OrderProduct orderProduct : orderProducts) {
                    Product product = productRepository.findById(orderProduct.getProduct().getId())
                            .orElseThrow(() -> new RuntimeException("Product not found"));

                    // Update cancelled order stock
                    product.setStock(product.getStock() + orderProduct.getQuantity());
                    productRepository.save(product);
                }


                orderRepository.save(order);
            } else {
                throw new RuntimeException("Order cannot be cancelled as it is not in a pending state.");
            }
        } else {
            throw new RuntimeException("Order not found");
        }
    }
}
