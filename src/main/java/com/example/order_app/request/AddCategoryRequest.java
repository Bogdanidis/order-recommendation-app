package com.example.order_app.request;

import com.example.order_app.model.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
@Data
public class AddCategoryRequest {

    private Long id;

    @NotBlank(message = "Category name is required")
    @Size(max = 255, message = "Category name must be less than 255 characters")
    private String name;

    @NotBlank(message = "Category description is required")
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;
}
