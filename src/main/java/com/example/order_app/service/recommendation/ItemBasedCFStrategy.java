//package com.example.order_app.service.recommendation;
//
//import com.example.order_app.dto.ProductDto;
//import com.example.order_app.model.Order;
//import com.example.order_app.model.OrderItem;
//import com.example.order_app.model.Product;
//import com.example.order_app.model.User;
//import com.example.order_app.repository.OrderRepository;
//import com.example.order_app.repository.ProductRepository;
//import com.example.order_app.service.product.IProductService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Component("itemBasedCF")
//@RequiredArgsConstructor
//public class ItemBasedCFStrategy implements RecommendationStrategy {
//
//    private final OrderRepository orderRepository;
//    private final ProductRepository productRepository;
//    private final IProductService productService;
//
//    @Override
//    public List<ProductDto> getRecommendations(User user, int numRecommendations) {
//        Set<Product> userProducts = getUserProducts(user);
//        Map<Product, Double> productSimilarities = calculateProductSimilarities(userProducts);
//
//        List<Product> recommendedProducts = productSimilarities.entrySet().stream()
//                .sorted(Map.Entry.<Product, Double>comparingByValue().reversed())
//                .limit(numRecommendations)
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toList());
//
//        return recommendedProducts.stream()
//                .map(productService::convertToDto)
//                .collect(Collectors.toList());
//    }
//
//    private Set<Product> getUserProducts(User user) {
//        return orderRepository.findByUser(user).stream()
//                .flatMap(order -> order.getOrderItems().stream())
//                .map(OrderItem::getProduct)
//                .collect(Collectors.toSet());
//    }
//
//    private Map<Product, Double> calculateProductSimilarities(Set<Product> userProducts) {
//        List<Product> allProducts = productRepository.findAll();
//        Map<Product, Double> similarities = new HashMap<>();
//
//        for (Product product : allProducts) {
//            if (!userProducts.contains(product)) {
//                double similarity = calculateSimilarity(product, userProducts);
//                similarities.put(product, similarity);
//            }
//        }
//
//        return similarities;
//    }
//
//    private double calculateSimilarity(Product product, Set<Product> userProducts) {
//        Set<User> productUsers = getUsersWhoOrderedProduct(product);
//        Set<User> userProductUsers = userProducts.stream()
//                .flatMap(p -> getUsersWhoOrderedProduct(p).stream())
//                .collect(Collectors.toSet());
//
//        Set<User> intersection = new HashSet<>(productUsers);
//        intersection.retainAll(userProductUsers);
//
//        Set<User> union = new HashSet<>(productUsers);
//        union.addAll(userProductUsers);
//
//        return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
//    }
//
//    private Set<User> getUsersWhoOrderedProduct(Product product) {
//        return orderRepository.findAll().stream()
//                .filter(order -> order.getOrderItems().stream()
//                        .anyMatch(item -> item.getProduct().equals(product)))
//                .map(Order::getUser)
//                .collect(Collectors.toSet());
//    }
//}