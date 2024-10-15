package com.example.order_app.service.recommendation;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.model.User;

import java.util.List;

public interface IRecommendationService {
    List<ProductDto> getRecommendationsForUser(User user, int numRecommendations);

}
