package com.example.order_app.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.Map;

@Data
public class RatingStatisticsDto {
    private Page<ProductRatingDto> content;
    private Map<Integer, Long> ratingCounts = new HashMap<>();
    private long totalRatings;

    public RatingStatisticsDto(Page<ProductRatingDto> ratings) {
        this.content = ratings;
        this.totalRatings = ratings.getTotalElements();

        // Initialize counts for all possible ratings
        for (int i = 1; i <= 5; i++) {
            ratingCounts.put(i, 0L);
        }

        // Count ratings
        ratings.getContent().forEach(rating ->
                ratingCounts.merge(rating.getRating(), 1L, Long::sum));
    }

    public long getRatingCount(int rating) {
        return ratingCounts.getOrDefault(rating, 0L);
    }

    public double getRatingPercentage(int rating) {
        if (totalRatings == 0) return 0.0;
        return (getRatingCount(rating) * 100.0) / totalRatings;
    }
}