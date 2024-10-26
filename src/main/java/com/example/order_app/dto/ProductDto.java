package com.example.order_app.dto;

import com.example.order_app.model.Category;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDto {
    private Long id;
    private String name;
    private String brand;
    private BigDecimal price;
    private Integer stock;
    private String description;
    private Category category;
    private List<ImageDto> images;
    private Double averageRating;
    private Long ratingCount;
    private List<ProductRatingDto> ratings;
}
