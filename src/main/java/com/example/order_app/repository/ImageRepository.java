package com.example.order_app.repository;

import com.example.order_app.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository  extends JpaRepository<Image, Long> {
    List<Image> findByProductId(Long id);
}
