package com.example.order_app.request;

import com.example.order_app.model.Category;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class UpdateProductRequest {
    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must be less than 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be non-negative")
    private Integer stock;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 10 digits and 2 decimal places")
    private BigDecimal price;

    @NotBlank(message = "Brand is required")
    @Size(max = 255, message = "Brand must be less than 255 characters")
    private String brand;

    @NotNull(message = "Category is required")
    private Category category;
}
