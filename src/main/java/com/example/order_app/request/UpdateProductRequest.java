package com.example.order_app.request;

import com.example.order_app.model.Category;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class UpdateProductRequest {
    private Long id;
    private String name;
    private String description;
    private Integer stock;
    private BigDecimal price;
    private String brand;
    private Category category;
}
