package com.example.order_app.request;

import com.example.order_app.model.Category;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AddProductRequest {
    private Long id;
    private String name;
    private String description;
    private Integer stock;
    private BigDecimal price;
    private String brand;
    private Category category;
}
