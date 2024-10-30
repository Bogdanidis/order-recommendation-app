package com.example.order_app.repository;

import com.example.order_app.enums.OrderStatus;
import com.example.order_app.model.Order;
import com.example.order_app.model.Product;
import com.example.order_app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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


    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END " +
            "FROM Order o JOIN o.orderItems oi " +
            "WHERE o.user.id = :userId " +
            "AND oi.product.id = :productId " +
            "AND o.orderStatus = 'DELIVERED'")
    boolean existsByUserIdAndOrderItemsProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Query("SELECT DISTINCT o.user FROM Order o")
    List<User> findAllUsersWithOrders();

    @Query("SELECT p FROM Order o JOIN o.orderItems oi JOIN oi.product p WHERE o.user.id = :userId")
    List<Product> findProductsPurchasedByUser(@Param("userId") Long userId);
}
