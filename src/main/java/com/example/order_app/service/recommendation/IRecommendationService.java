package com.example.order_app.service.recommendation;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.model.Product;
import com.example.order_app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IRecommendationService {
    Page<ProductDto> getRecommendationsForUser(User user, Pageable pageable);
    Page<ProductDto> getCartBasedRecommendations(List<Product> cartItems, Pageable pageable);
}
