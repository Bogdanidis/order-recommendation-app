package com.example.order_app.repository;

import com.example.order_app.model.ProductRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRatingRepository extends JpaRepository<ProductRating, Long> {

    Page<ProductRating> findByProductId(Long productId, Pageable pageable);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM ProductRating r WHERE r.product.id = :productId")
    double calculateAverageRating(Long productId);

    long countByProductId(Long productId);
}