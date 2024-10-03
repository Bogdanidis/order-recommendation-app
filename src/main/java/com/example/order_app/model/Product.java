package com.example.order_app.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @Column(name = "id")
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

    @OneToMany(mappedBy = "product",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="category_id")
    private Category category ;


    public Product(String name, String description, Integer stock, BigDecimal price,String brand, Category category) {
        this.name = name;
        this.description = description;
        this.stock = stock;
        this.price = price;
        this.brand=brand;
        this.category = category;
    }


}
