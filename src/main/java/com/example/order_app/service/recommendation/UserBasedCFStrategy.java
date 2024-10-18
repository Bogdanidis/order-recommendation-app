package com.example.order_app.service.recommendation;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.model.Order;
import com.example.order_app.model.OrderItem;
import com.example.order_app.model.Product;
import com.example.order_app.model.User;
import com.example.order_app.repository.OrderRepository;
import com.example.order_app.repository.ProductRepository;
import com.example.order_app.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component("userBasedCF")
@RequiredArgsConstructor
public class UserBasedCFStrategy implements RecommendationStrategy {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final IProductService productService;

    @Override
    public List<ProductDto> getRecommendations(User user, int numRecommendations) {
        // Get the products the user has ordered
        Set<Product> userProducts = getUserProducts(user);

        // Find similar users and their similarity scores
        Map<User, Double> similarUsers = findSimilarUsers(user, userProducts);

        // Get products ordered by similar users that the current user hasn't ordered
        Map<Product, Double> recommendedProducts = getSimilarUsersProducts(similarUsers, userProducts);

        // Convert to DTOs, sort by similarity score, and limit to the requested number of recommendations
        return recommendedProducts.entrySet().stream()
                .sorted(Map.Entry.<Product, Double>comparingByValue().reversed())
                .limit(numRecommendations)
                .map(entry -> {
                    ProductDto dto = productService.convertToDto(entry.getKey());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all products that the user has ordered.
     */
    private Set<Product> getUserProducts(User user) {
        return orderRepository.findByUserId(user.getId()).stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(OrderItem::getProduct)
                .collect(Collectors.toSet());
    }

    /**
     * Finds users who have ordered at least one product in common with the given user.
     * Returns a map of similar users and their similarity scores.
     */
    private Map<User, Double> findSimilarUsers(User user, Set<Product> userProducts) {
        List<User> allUsers = orderRepository.findAll().stream()
                .map(Order::getUser)
                .filter(u -> !u.equals(user))
                .distinct()
                .toList();

        Map<User, Double> similarUsers = new HashMap<>();
        for (User otherUser : allUsers) {
            Set<Product> otherUserProducts = getUserProducts(otherUser);
            double similarity = calculateJaccardSimilarity(userProducts, otherUserProducts);
            if (similarity > 0) {
                similarUsers.put(otherUser, similarity);
            }
        }

        return similarUsers;
    }

    /**
     * Calculates the Jaccard similarity between two sets of products.
     */
    private double calculateJaccardSimilarity(Set<Product> set1, Set<Product> set2) {
        Set<Product> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<Product> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }

    /**
     * Gets products ordered by similar users that the current user hasn't ordered yet.
     * Returns a map of products and their weighted similarity scores.
     */
    private Map<Product, Double> getSimilarUsersProducts(Map<User, Double> similarUsers, Set<Product> userProducts) {
        Map<Product, Double> recommendedProducts = new HashMap<>();

        for (Map.Entry<User, Double> entry : similarUsers.entrySet()) {
            User similarUser = entry.getKey();
            Double similarity = entry.getValue();

            Set<Product> similarUserProducts = getUserProducts(similarUser);
            for (Product product : similarUserProducts) {
                if (!userProducts.contains(product)) {
                    recommendedProducts.merge(product, similarity, Double::sum);
                }
            }
        }

        return recommendedProducts;
    }
}