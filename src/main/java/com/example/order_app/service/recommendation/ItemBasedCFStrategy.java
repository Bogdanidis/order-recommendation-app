package com.example.order_app.service.recommendation;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.model.Order;
import com.example.order_app.model.OrderItem;
import com.example.order_app.model.Product;
import com.example.order_app.model.User;
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

@Component("itemBasedCF")
@RequiredArgsConstructor
public class ItemBasedCFStrategy implements RecommendationStrategy {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductRatingRepository ratingRepository;
    private final IProductService productService;

    @Override
    public Page<ProductDto> getRecommendations(User user, Pageable pageable, boolean includeRatingWeight) {
        List<Product> userProducts = orderRepository.findProductsPurchasedByUser(user.getId());
        if (userProducts.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Product> allProducts = productRepository.findAll();
        List<ScoredProduct> recommendations = new ArrayList<>();

        for (Product candidate : allProducts) {
            if (!userProducts.contains(candidate)) {
                double score = calculateSimilarityScore(candidate, userProducts, includeRatingWeight);
                recommendations.add(new ScoredProduct(candidate, score));
            }
        }

        Collections.sort(recommendations);

        System.out.println("Total recommendations before pagination: {}");
        System.out.println(recommendations.size());
        System.out.println("Page size requested: {}");
        System.out.println(pageable.getPageSize());
        System.out.println("Current page requested: {}");
        System.out.println(pageable.getPageNumber());


        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), recommendations.size());

        List<ProductDto> paginatedResults = recommendations.subList(start, end).stream()
                .map(scored -> productService.convertToDto(scored.getProduct()))
                .collect(Collectors.toList());

        System.out.println("Returning {} results for page {}");
        System.out.println(paginatedResults.size());
        System.out.println(pageable.getPageNumber());

        PageImpl<ProductDto> page = new PageImpl<>(
                paginatedResults,
                pageable,
                recommendations.size()
        );
        System.out.println("Total pages calculated: {}");
        System.out.println(page.getTotalPages());

        return page;
    }

    private double calculateSimilarityScore(Product candidate, List<Product> userProducts, boolean includeRatingWeight) {
        double maxSimilarity = userProducts.stream()
                .mapToDouble(userProduct -> calculateProductSimilarity(candidate, userProduct))
                .max()
                .orElse(0.0);

        if (includeRatingWeight) {
            Double avgRating = ratingRepository.getAverageRating(candidate.getId());
            Long ratingCount = ratingRepository.getRatingCount(candidate.getId());

            if (avgRating != null && ratingCount != null) {
                double ratingScore = (avgRating / 5.0) * Math.min(ratingCount / 10.0, 1.0);
                return maxSimilarity * 0.7 + ratingScore * 0.3;
            }
        }

        return maxSimilarity;
    }

    private double calculateProductSimilarity(Product p1, Product p2) {
        // Category similarity
        boolean sameCategory = p1.getCategory().equals(p2.getCategory());

        // Brand similarity
        boolean sameBrand = p1.getBrand().equals(p2.getBrand());

        // Price similarity (within 20% range)
        boolean similarPrice = Math.abs(p1.getPrice().doubleValue() - p2.getPrice().doubleValue()) /
                p1.getPrice().doubleValue() <= 0.2;

        return (sameCategory ? 0.5 : 0) + (sameBrand ? 0.3 : 0) + (similarPrice ? 0.2 : 0);
    }
}

