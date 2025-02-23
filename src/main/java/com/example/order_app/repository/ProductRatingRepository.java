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

public interface ProductRatingRepository extends BaseRepository<ProductRating, Long> {
    @Query("SELECT r FROM ProductRating r WHERE r.product.id = :productId AND r.deleted = false")
    Page<ProductRating> findByProductId(@Param("productId") Long productId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ProductRating r " +
            "WHERE r.user.id = :userId AND r.product.id = :productId AND r.deleted = false")
    boolean existsByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM ProductRating r WHERE r.product.id = :productId AND r.deleted = false")
    double calculateAverageRating(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM ProductRating r WHERE r.product.id = :productId AND r.deleted = false")
    long countByProductId(@Param("productId") Long productId);

    @Query("SELECT r FROM ProductRating r WHERE r.user.id = :userId AND r.deleted = false")
    List<ProductRating> findByUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM ProductRating r WHERE r.user.id = :userId AND r.product.id = :productId AND r.deleted = false")
    Optional<ProductRating> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Query("SELECT AVG(r.rating) FROM ProductRating r WHERE r.product.id = :productId AND r.deleted = false")
    Double getAverageRating(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM ProductRating r WHERE r.product.id = :productId AND r.deleted = false")
    Long getRatingCount(@Param("productId") Long productId);
}