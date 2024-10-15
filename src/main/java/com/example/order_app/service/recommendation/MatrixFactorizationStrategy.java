//package com.example.order_app.service.recommendation;
//
//import com.example.order_app.dto.ProductDto;
//import com.example.order_app.model.Order;
//import com.example.order_app.model.Product;
//import com.example.order_app.model.User;
//import com.example.order_app.repository.OrderRepository;
//import com.example.order_app.repository.ProductRepository;
//import com.example.order_app.repository.UserRepository;
//import com.example.order_app.service.product.IProductService;
//import lombok.RequiredArgsConstructor;
//import org.apache.commons.math3.linear.MatrixUtils;
//import org.apache.commons.math3.linear.RealMatrix;
//import org.apache.commons.math3.linear.SingularValueDecomposition;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Component("matrixFactorization")
//@RequiredArgsConstructor
//public class MatrixFactorizationStrategy implements RecommendationStrategy {
//
//    private final OrderRepository orderRepository;
//    private final ProductRepository productRepository;
//    private final UserRepository userRepository;
//    private final IProductService productService;
//
//    private static final int LATENT_FACTORS = 10;
//
//    @Override
//    public List<ProductDto> getRecommendations(User user, int numRecommendations) {
//        List<User> users = userRepository.findAll();
//        List<Product> products = productRepository.findAll();
//
//        RealMatrix ratingMatrix = buildRatingMatrix(users, products);
//        SingularValueDecomposition svd = new SingularValueDecomposition(ratingMatrix);
//
//        RealMatrix userFeatures = svd.getU().getSubMatrix(0, users.size() - 1, 0, LATENT_FACTORS - 1);
//        RealMatrix productFeatures = svd.getV().getSubMatrix(0, products.size() - 1, 0, LATENT_FACTORS - 1);
//
//        int userIndex = users.indexOf(user);
//        double[] userVector = userFeatures.getRow(userIndex);
//
//        List<ProductDto> recommendations = new ArrayList<>();
//        for (int i = 0; i < products.size(); i++) {
//            if (!hasUserOrderedProduct(user, products.get(i))) {
//                double[] productVector = productFeatures.getRow(i);
//                double predictedRating = dotProduct(userVector, productVector);
//                ProductDto productDto = productService.convertToDto(products.get(i));
//                productDto.setPredictedRating(predictedRating);
//                recommendations.add(productDto);
//            }
//        }
//
//        return recommendations.stream()
//                .sorted(Comparator.comparingDouble(ProductDto::getPredictedRating).reversed())
//                .limit(numRecommendations)
//                .collect(Collectors.toList());
//    }
//
//    private RealMatrix buildRatingMatrix(List<User> users, List<Product> products) {
//        double[][] ratingData = new double[users.size()][products.size()];
//        for (int i = 0; i < users.size(); i++) {
//            for (int j = 0; j < products.size(); j++) {
//                ratingData[i][j] = getRating(users.get(i), products.get(j));
//            }
//        }
//        return MatrixUtils.createRealMatrix(ratingData);
//    }
//
//    private double getRating(User user, Product product) {
//        return orderRepository.findByUser(user).stream()
//                .flatMap(order -> order.getOrderItems().stream())
//                .filter(item -> item.getProduct().equals(product))
//                .findFirst()
//                .map(item -> 1.0)  // You might want to use a more nuanced rating system
//                .orElse(0.0);
//    }
//
//    private boolean hasUserOrderedProduct(User user, Product product) {
//        return orderRepository.findByUser(user).stream()
//                .flatMap(order -> order.getOrderItems().stream())
//                .anyMatch(item -> item.getProduct().equals(product));
//    }
//
//    private double dotProduct(double[] a, double[] b) {
//        double sum = 0;
//        for (int i = 0; i < a.length; i++) {
//            sum += a[i] * b[i];
//        }
//        return sum;
//    }
//}