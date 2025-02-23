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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends BaseRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.deleted = false")
    List<Order> findByUserId(@Param("userId") Long userId);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.deleted = false")
    Page<Order> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.id = :orderId AND o.user.id = :userId AND o.deleted = false")
    Optional<Order> findByIdAndUserId(@Param("orderId") Long orderId, @Param("userId") Long userId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate = :today AND o.orderStatus != :orderStatus AND o.deleted = false")
    Long countByOrderDateAndOrderStatusNot(@Param("today") LocalDate today, @Param("orderStatus") OrderStatus orderStatus);

    @Query("SELECT o FROM Order o WHERE o.orderDate = :date AND o.orderStatus != :status AND o.deleted = false")
    List<Order> findByOrderDateAndOrderStatusNot(@Param("date") LocalDate date, @Param("status") OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId AND o.deleted = false")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Order o " +
            "JOIN o.orderItems oi " +
            "WHERE o.user.id = :userId " +
            "AND oi.product.id = :productId " +
            "AND o.orderStatus = 'DELIVERED' " +
            "AND o.deleted = false")
    boolean existsByUserIdAndOrderItemsProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Query("SELECT DISTINCT o.user FROM Order o WHERE o.deleted = false")
    List<User> findAllUsersWithOrders();

    @Query("SELECT p FROM Order o JOIN o.orderItems oi JOIN oi.product p WHERE o.user.id = :userId AND o.deleted = false")
    List<Product> findProductsPurchasedByUser(@Param("userId") Long userId);

    @Query("SELECT o FROM Order o WHERE o.orderStatus != :orderStatus AND o.deleted = false")
    List<Order> findByOrderStatusNot(@Param("orderStatus") OrderStatus orderStatus);
}
