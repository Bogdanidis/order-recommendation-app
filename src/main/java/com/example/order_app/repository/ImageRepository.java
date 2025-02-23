package com.example.order_app.repository;

import com.example.order_app.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepository extends BaseRepository<Image, Long> {
    @Query("SELECT i FROM Image i WHERE i.product.id = :id AND i.deleted = false")
    List<Image> findByProductId(@Param("id") Long id);
}
