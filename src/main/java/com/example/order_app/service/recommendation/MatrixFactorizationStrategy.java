package com.example.order_app.service.recommendation;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.model.Product;
import com.example.order_app.model.User;
import com.example.order_app.repository.OrderRepository;
import com.example.order_app.repository.ProductRepository;
import com.example.order_app.repository.UserRepository;
import com.example.order_app.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component("matrixFactorization")
@RequiredArgsConstructor
public class MatrixFactorizationStrategy implements RecommendationStrategy {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final IProductService productService;

    private static final int LATENT_FACTORS = 10;

    @Override
    public List<ProductDto> getRecommendations(User user, int numRecommendations) {
        List<User> users = userRepository.findAll();
        List<Product> products = productRepository.findAll();

        // Build the user-item interaction matrix
        RealMatrix interactionMatrix = buildInteractionMatrix(users, products);
        // Perform Singular Value Decomposition
        SingularValueDecomposition svd = new SingularValueDecomposition(interactionMatrix);

        // Extract user and product feature matrices
        RealMatrix userFeatures = svd.getU().getSubMatrix(0, users.size() - 1, 0, LATENT_FACTORS - 1);
        RealMatrix productFeatures = svd.getV().getSubMatrix(0, products.size() - 1, 0, LATENT_FACTORS - 1);

        // Get the feature vector for the current user
        int userIndex = users.indexOf(user);
        double[] userVector = userFeatures.getRow(userIndex);

        // Calculate similarity scores for all products
        List<ProductDto> recommendations = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            if (!hasUserOrderedProduct(user, products.get(i))) {
                double[] productVector = productFeatures.getRow(i);
                double similarityScore = cosineSimilarity(userVector, productVector);
                ProductDto productDto = productService.convertToDto(products.get(i));
                recommendations.add(productDto);
            }
        }

        // Sort by similarity score and return top recommendations
        return recommendations.stream()
                .sorted(Comparator.comparingDouble(p -> -cosineSimilarity(userVector, productFeatures.getRow(products.indexOf(productService.getProductById(p.getId()))))))
                .limit(numRecommendations)
                .collect(Collectors.toList());
    }

    /**
     * Builds the user-item interaction matrix.
     */
    private RealMatrix buildInteractionMatrix(List<User> users, List<Product> products) {
        double[][] interactionData = new double[users.size()][products.size()];
        for (int i = 0; i < users.size(); i++) {
            for (int j = 0; j < products.size(); j++) {
                interactionData[i][j] = hasUserOrderedProduct(users.get(i), products.get(j)) ? 1.0 : 0.0;
            }
        }
        return MatrixUtils.createRealMatrix(interactionData);
    }

    /**
     * Checks if a user has ordered a specific product.
     */
    private boolean hasUserOrderedProduct(User user, Product product) {
        return orderRepository.findByUserId(user.getId()).stream()
                .flatMap(order -> order.getOrderItems().stream())
                .anyMatch(item -> item.getProduct().equals(product));
    }

    /**
     * Calculates the cosine similarity between two vectors.
     */
    private double cosineSimilarity(double[] a, double[] b) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += Math.pow(a[i], 2);
            normB += Math.pow(b[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}