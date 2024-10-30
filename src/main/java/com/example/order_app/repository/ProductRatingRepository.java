package com.example.order_app.repository;

import com.example.order_app.model.ProductRating;
import com.example.order_app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRatingRepository extends JpaRepository<ProductRating, Long> {

    Page<ProductRating> findByProductId(Long productId, Pageable pageable);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM ProductRating r WHERE r.product.id = :productId")
    double calculateAverageRating(Long productId);

    long countByProductId(Long productId);


    List<ProductRating> findByUserId(Long userId);
    Optional<ProductRating> findByUserIdAndProductId(Long userId, Long productId);

    @Query("SELECT AVG(r.rating) FROM ProductRating r WHERE r.product.id = :productId")
    Double getAverageRating(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM ProductRating r WHERE r.product.id = :productId")
    Long getRatingCount(@Param("productId") Long productId);

}