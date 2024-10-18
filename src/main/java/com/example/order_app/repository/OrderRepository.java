package com.example.order_app.repository;

import com.example.order_app.enums.OrderStatus;
import com.example.order_app.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    Page<Order> findByUserId(Long userId, Pageable pageable);

    Optional<Order> findByIdAndUserId(Long orderId, Long userId);


    Long countByOrderDateAndOrderStatusNot(LocalDate today, OrderStatus orderStatus);
    List<Order> findByOrderDateAndOrderStatusNot(LocalDate date, OrderStatus status);

    long countByUserId(Long userId);
}
