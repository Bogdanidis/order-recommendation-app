package com.example.order_app.service.recommendation;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.model.User;

import java.util.List;

public interface RecommendationStrategy {
    /**
     * Generate product recommendations for a given user.
     *
     * @param user The user to generate recommendations for
     * @param numRecommendations The number of recommendations to generate
     * @return A list of recommended ProductDto objects
     */
    List<ProductDto> getRecommendations(User user, int numRecommendations);
}
