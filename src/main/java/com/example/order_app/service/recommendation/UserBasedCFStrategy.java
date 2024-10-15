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
        Set<Product> userProducts = getUserProducts(user);
        List<User> similarUsers = findSimilarUsers(user, userProducts);
        Set<Product> recommendedProducts = getSimilarUsersProducts(similarUsers, userProducts);

        return recommendedProducts.stream()
                .map(productService::convertToDto)
                .limit(numRecommendations)
                .collect(Collectors.toList());
    }

    private Set<Product> getUserProducts(User user) {
        return orderRepository.findByUserId(user.getId()).stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(OrderItem::getProduct)
                .collect(Collectors.toSet());
    }

    private List<User> findSimilarUsers(User user, Set<Product> userProducts) {
        return orderRepository.findAll().stream()
                .filter(order -> !order.getUser().equals(user))
                .filter(order -> order.getOrderItems().stream()
                        .anyMatch(item -> userProducts.contains(item.getProduct())))
                .map(Order::getUser)
                .distinct()
                .collect(Collectors.toList());
    }

    private Set<Product> getSimilarUsersProducts(List<User> similarUsers, Set<Product> userProducts) {
        return similarUsers.stream()
                .flatMap(u -> orderRepository.findByUserId(u.getId()).stream())
                .flatMap(order -> order.getOrderItems().stream())
                .map(OrderItem::getProduct)
                .filter(product -> !userProducts.contains(product))
                .collect(Collectors.toSet());
    }
}