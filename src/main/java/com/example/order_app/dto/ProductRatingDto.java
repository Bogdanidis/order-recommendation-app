package com.example.order_app.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductRatingDto {
    private Long id;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;

    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String comment;

    private String userFullName;
    private Long userId;  // Added to help identify user's own review
    private String formattedDate;
}