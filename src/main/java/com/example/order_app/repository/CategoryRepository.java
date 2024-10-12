package com.example.order_app.repository;

import com.example.order_app.model.Category;
import com.example.order_app.request.AddCategoryRequest;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);

    boolean existsByName(String name);

}