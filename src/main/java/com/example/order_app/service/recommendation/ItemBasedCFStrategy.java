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

@Component("itemBasedCF")
@RequiredArgsConstructor
public class ItemBasedCFStrategy implements RecommendationStrategy {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final IProductService productService;

    @Override
    public List<ProductDto> getRecommendations(User user, int numRecommendations) {
        // Get products the user has ordered
        Set<Product> userProducts = getUserProducts(user);
        // Calculate similarities between user's products and other products
        Map<Product, Double> productSimilarities = calculateProductSimilarities(userProducts);

        // Sort products by similarity and select top recommendations
        List<Product> recommendedProducts = productSimilarities.entrySet().stream()
                .sorted(Map.Entry.<Product, Double>comparingByValue().reversed())
                .limit(numRecommendations)
                .map(Map.Entry::getKey)
                .toList();

        // Convert to DTOs
        return recommendedProducts.stream()
                .map(productService::convertToDto)
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
     * Calculates similarity scores between the user's products and all other products.
     */
    private Map<Product, Double> calculateProductSimilarities(Set<Product> userProducts) {
        List<Product> allProducts = productRepository.findAll();
        Map<Product, Double> similarities = new HashMap<>();

        for (Product product : allProducts) {
            if (!userProducts.contains(product)) {
                double similarity = calculateSimilarity(product, userProducts);
                similarities.put(product, similarity);
            }
        }

        return similarities;
    }

    /**
     * Calculates the Jaccard similarity between a product and the user's products.
     */
    private double calculateSimilarity(Product product, Set<Product> userProducts) {
        Set<User> productUsers = getUsersWhoOrderedProduct(product);
        Set<User> userProductUsers = userProducts.stream()
                .flatMap(p -> getUsersWhoOrderedProduct(p).stream())
                .collect(Collectors.toSet());

        Set<User> intersection = new HashSet<>(productUsers);
        intersection.retainAll(userProductUsers);

        Set<User> union = new HashSet<>(productUsers);
        union.addAll(userProductUsers);

        return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
    }

    /**
     * Finds all users who have ordered a specific product.
     */
    private Set<User> getUsersWhoOrderedProduct(Product product) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getOrderItems().stream()
                        .anyMatch(item -> item.getProduct().equals(product)))
                .map(Order::getUser)
                .collect(Collectors.toSet());
    }

    
}