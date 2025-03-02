package com.example.order_app.repository;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;


public interface ProductRepository extends BaseRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.category.name = :category AND p.deleted = false")
    List<Product> findByCategoryName(@Param("category") String category);

    @Query("SELECT p FROM Product p WHERE p.brand = :brand AND p.deleted = false")
    List<Product> findByBrand(@Param("brand") String brand);

    @Query("SELECT p FROM Product p WHERE p.category.name = :category AND p.brand = :brand AND p.deleted = false")
    List<Product> findByCategoryNameAndBrand(@Param("category") String category, @Param("brand") String brand);

    @Query("SELECT p FROM Product p WHERE p.name = :name AND p.deleted = false")
    List<Product> findByName(@Param("name") String name);

    @Query("SELECT p FROM Product p WHERE p.brand = :brand AND p.name = :name AND p.deleted = false")
    List<Product> findByBrandAndName(@Param("brand") String brand, @Param("name") String name);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.brand = :brand AND p.name = :name AND p.deleted = false")
    Long countByBrandAndName(@Param("brand") String brand, @Param("name") String name);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.name = :name AND p.deleted = false")
    Long countByName(@Param("name") String name);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.name = :category AND p.brand = :brand AND p.deleted = false")
    Long countByCategoryNameAndBrand(@Param("category") String category, @Param("brand") String brand);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.brand = :brand AND p.deleted = false")
    Long countByBrand(@Param("brand") String brand);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.name = :category AND p.deleted = false")
    Long countByCategoryName(@Param("category") String category);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p WHERE p.name = :name AND p.brand = :brand AND p.deleted = false")
    boolean existsByNameAndBrand(@Param("name") String name, @Param("brand") String brand);

    @Query("SELECT p FROM Product p WHERE p.brand LIKE %:brand% AND p.name LIKE %:name% AND p.category.name LIKE %:category% AND p.deleted = false")
    Page<Product> findByBrandContainingIgnoreCaseAndNameContainingIgnoreCaseAndCategoryNameContainingIgnoreCase(
            String brand, String name, String category, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id IN :categoryIds AND p.deleted = false")
    List<Product> findProductsByCategoryIds(@Param("categoryIds") Set<Long> categoryIds);

    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

}
