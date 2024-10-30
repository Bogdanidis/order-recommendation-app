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

@Component("userBasedCF")
@RequiredArgsConstructor
public class UserBasedCFStrategy implements RecommendationStrategy {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductRatingRepository ratingRepository;
    private final IProductService productService;

    @Override
    public Page<ProductDto> getRecommendations(User user, Pageable pageable, boolean includeRatingWeight) {
        List<Product> userProducts = orderRepository.findProductsPurchasedByUser(user.getId());
        List<User> similarUsers = findSimilarUsers(user, userProducts);

        List<ScoredProduct> recommendedProducts = new ArrayList<>();
        Set<Long> recommendedProductIds = new HashSet<>();

        for (User similarUser : similarUsers) {
            List<Product> similarUserProducts = orderRepository.findProductsPurchasedByUser(similarUser.getId());
            for (Product product : similarUserProducts) {
                if (!userProducts.contains(product) && !recommendedProductIds.contains(product.getId())) {
                    double score = calculateRecommendationScore(product, similarUser, includeRatingWeight);
                    recommendedProducts.add(new ScoredProduct(product, score));
                    recommendedProductIds.add(product.getId());
                }
            }
        }

        Collections.sort(recommendedProducts);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), recommendedProducts.size());

        List<ProductDto> paginatedResults = recommendedProducts.subList(start, end).stream()
                .map(scored -> productService.convertToDto(scored.getProduct()))
                .collect(Collectors.toList());

        return new PageImpl<>(
                paginatedResults,
                pageable,
                recommendedProducts.size()
        );
    }

    private List<User> findSimilarUsers(User user, List<Product> userProducts) {
        List<User> allUsers = orderRepository.findAllUsersWithOrders();
        return allUsers.stream()
                .filter(u -> !u.equals(user))
                .sorted((u1, u2) -> Double.compare(
                        calculateUserSimilarity(userProducts, orderRepository.findProductsPurchasedByUser(u2.getId())),
                        calculateUserSimilarity(userProducts, orderRepository.findProductsPurchasedByUser(u1.getId()))
                ))
                .limit(10) // Consider top 10 similar users
                .collect(Collectors.toList());
    }

    private double calculateUserSimilarity(List<Product> products1, List<Product> products2) {
        Set<Long> set1 = products1.stream().map(Product::getId).collect(Collectors.toSet());
        Set<Long> set2 = products2.stream().map(Product::getId).collect(Collectors.toSet());

        Set<Long> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<Long> union = new HashSet<>(set1);
        union.addAll(set2);

        return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
    }

    private double calculateRecommendationScore(Product product, User similarUser, boolean includeRatingWeight) {
        double baseScore = 1.0;

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