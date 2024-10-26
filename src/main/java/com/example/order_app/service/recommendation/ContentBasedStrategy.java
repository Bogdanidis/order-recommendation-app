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

@Component("contentBased")
@RequiredArgsConstructor
public class ContentBasedStrategy implements RecommendationStrategy {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final IProductService productService;

    @Override
    public List<ProductDto> getRecommendations(User user, int numRecommendations) {
        // Get categories of products the user has interacted with
        Set<Long> userCategories = getUserCategories(user);

        // If no categories found, return empty list
        if (userCategories.isEmpty()) {
            return Collections.emptyList();
        }

        // Get products from these categories, excluding products the user has already purchased
        Set<Product> userProducts = getUserProducts(user);
        List<Product> recommendedProducts = productRepository.findProductsByCategoryIds(userCategories).stream()
                .filter(product -> !userProducts.contains(product))
                .collect(Collectors.toList());

        // Calculate category frequency to prioritize recommendations
        Map<Long, Long> categoryFrequency = calculateCategoryFrequency(user);

        // Sort products by category frequency (prioritize products from frequently bought categories)
        recommendedProducts.sort((p1, p2) -> {
            Long freq1 = categoryFrequency.getOrDefault(p1.getCategory().getId(), 0L);
            Long freq2 = categoryFrequency.getOrDefault(p2.getCategory().getId(), 0L);
            return freq2.compareTo(freq1);
        });

        // Convert to DTOs and limit to the requested number
        return recommendedProducts.stream()
                .limit(numRecommendations)
                .map(productService::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get the categories of products the user has interacted with
     */
    private Set<Long> getUserCategories(User user) {
        Set<Long> categories = new HashSet<>();

        // Add categories from user's orders
        List<Order> userOrders = orderRepository.findByUserId(user.getId());
        for (Order order : userOrders) {
            order.getOrderItems().forEach(item ->
                    categories.add(item.getProduct().getCategory().getId()));
        }

        return categories;
    }

    /**
     * Get all products the user has purchased
     */
    private Set<Product> getUserProducts(User user) {
        return orderRepository.findByUserId(user.getId()).stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(OrderItem::getProduct)
                .collect(Collectors.toSet());
    }

    /**
     * Calculate how frequently the user has bought from each category
     */
    private Map<Long, Long> calculateCategoryFrequency(User user) {
        return orderRepository.findByUserId(user.getId()).stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(item -> item.getProduct().getCategory().getId())
                .collect(Collectors.groupingBy(
                        categoryId -> categoryId,
                        Collectors.counting()
                ));
    }
}