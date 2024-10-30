package com.example.order_app.service.recommendation;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.model.*;
import com.example.order_app.repository.OrderRepository;
import com.example.order_app.repository.ProductRatingRepository;
import com.example.order_app.repository.ProductRepository;
import com.example.order_app.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component("contentBased")
@RequiredArgsConstructor
public class ContentBasedStrategy implements RecommendationStrategy {
    private final ProductRepository productRepository;
    private final ProductRatingRepository ratingRepository;
    private final IProductService productService;

    @Override
    public Page<ProductDto> getRecommendations(User user, Pageable pageable, boolean includeRatingWeight) {
        Map<Category, Double> categoryPreferences = calculateCategoryPreferences(user);
        Map<String, Double> brandPreferences = calculateBrandPreferences(user);

        List<Product> allProducts = productRepository.findAll();
        List<ScoredProduct> recommendations = new ArrayList<>();

        for (Product product : allProducts) {
            double score = calculateProductScore(product, categoryPreferences, brandPreferences, includeRatingWeight);
            recommendations.add(new ScoredProduct(product, score));
        }

        Collections.sort(recommendations);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), recommendations.size());

        List<ProductDto> paginatedResults = recommendations.subList(start, end).stream()
                .map(scored -> productService.convertToDto(scored.getProduct()))
                .collect(Collectors.toList());

        return new PageImpl<>(
                paginatedResults,
                pageable,
                recommendations.size()
        );
    }

    private Map<Category, Double> calculateCategoryPreferences(User user) {
        List<Order> orders = user.getOrders();
        Map<Category, Integer> categoryCounts = new HashMap<>();

        orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .forEach(item -> {
                    Category category = item.getProduct().getCategory();
                    categoryCounts.merge(category, 1, Integer::sum);
                });

        return normalizePreferences(categoryCounts);
    }

    private Map<String, Double> calculateBrandPreferences(User user) {
        Map<String, Integer> brandCounts = new HashMap<>();

        user.getOrders().stream()
                .flatMap(order -> order.getOrderItems().stream())
                .forEach(item -> {
                    String brand = item.getProduct().getBrand();
                    brandCounts.merge(brand, 1, Integer::sum);
                });

        return normalizePreferences(brandCounts);
    }

    private <T> Map<T, Double> normalizePreferences(Map<T, Integer> counts) {
        double total = counts.values().stream().mapToInt(Integer::intValue).sum();
        return counts.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue() / total
                ));
    }

    private double calculateProductScore(Product product,
                                         Map<Category, Double> categoryPreferences,
                                         Map<String, Double> brandPreferences,
                                         boolean includeRatingWeight) {
        double categoryScore = categoryPreferences.getOrDefault(product.getCategory(), 0.0);
        double brandScore = brandPreferences.getOrDefault(product.getBrand(), 0.0);
        double baseScore = (categoryScore * 0.6) + (brandScore * 0.4);

        if (includeRatingWeight) {
            Double avgRating = ratingRepository.getAverageRating(product.getId());
            Long ratingCount = ratingRepository.getRatingCount(product.getId());

            if (avgRating != null && ratingCount != null) {
                double ratingScore = (avgRating / 5.0) * Math.min(ratingCount / 10.0, 1.0);
                return baseScore * 0.7 + ratingScore * 0.3;
            }
        }

        return baseScore;
    }
}