package com.example.order_app.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "products")
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;


    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "brand", nullable = false)
    private String brand;

    // Images should be soft deleted with product
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @BatchSize(size = 20) // Batch fetch images
    private List<Image> images;

    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category ;

    // Ratings should be soft deleted with product
    @OneToMany(mappedBy = "product",
            cascade = CascadeType.ALL)
    private List<ProductRating> ratings = new ArrayList<>();


    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "rating_count")
    private Long ratingCount = 0L;


    public Product(String name,String brand, Integer stock, BigDecimal price, String description, Category category) {
        this.name = name;
        this.brand=brand;
        this.stock = stock;
        this.price = price;
        this.description = description;
        this.category = category;
    }


}
