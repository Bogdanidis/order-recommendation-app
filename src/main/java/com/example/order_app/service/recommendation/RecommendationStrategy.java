package com.example.order_app.service.recommendation;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecommendationStrategy {
    Page<ProductDto> getRecommendations(User user, Pageable pageable, boolean includeRatingWeight);

}
