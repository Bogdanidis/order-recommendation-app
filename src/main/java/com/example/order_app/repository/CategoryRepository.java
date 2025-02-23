package com.example.order_app.repository;

import com.example.order_app.model.Category;
import com.example.order_app.request.AddCategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface CategoryRepository extends BaseRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.name = :name AND c.deleted = false")
    Category findByName(@Param("name") String name);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE c.name = :name AND c.deleted = false")
    boolean existsByName(@Param("name") String name);

    @Query("SELECT c FROM Category c WHERE c.name LIKE %:name% AND c.deleted = false")
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);
}