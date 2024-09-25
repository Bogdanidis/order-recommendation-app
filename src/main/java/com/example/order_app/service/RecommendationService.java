package com.example.order_app.service;

import com.example.order_app.model.Product;
import com.example.order_app.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ProductRepository productRepository;

    // Other methods

    public List<Product> getRecommendedProducts() {
        // For now, simply fetch a subset of products or all products.
        // In future, this could be replaced with a recommendation algorithm.
        return productRepository.findAll(); // Example query
    }
}
