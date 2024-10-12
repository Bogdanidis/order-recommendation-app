package com.example.order_app.repository;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long>{

    List<Product> findByCategoryName(String category);
    List<Product> findByBrand(String brand);

    List<Product> findByCategoryNameAndBrand(String category, String brand);

    List<Product> findByName(String name);

    List<Product> findByBrandAndName(String brand, String name);

    Long countByBrandAndName(String brand, String name);

    Long countByName(String name);

    Long countByCategoryNameAndBrand(String category, String brand);

    Long countByBrand(String brand);

    Long countByCategoryName(String category);

    boolean existsByNameAndBrand(String name, String brand);

    Page<Product> findByBrandContainingIgnoreCaseAndNameContainingIgnoreCaseAndCategoryNameContainingIgnoreCase(
            String brand, String name, String category, Pageable pageable);
}
