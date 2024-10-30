package com.example.order_app.service.recommendation;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.model.Product;
import com.example.order_app.model.ProductRating;
import com.example.order_app.model.User;
import com.example.order_app.repository.OrderRepository;
import com.example.order_app.repository.ProductRatingRepository;
import com.example.order_app.repository.ProductRepository;
import com.example.order_app.repository.UserRepository;
import com.example.order_app.service.product.IProductService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component("matrixFactorization")
@RequiredArgsConstructor
@Slf4j
public class MatrixFactorizationStrategy implements RecommendationStrategy {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductRatingRepository ratingRepository;
    private final IProductService productService;

    private static final int LATENT_FACTORS = 10;
    private static final double LEARNING_RATE = 0.01;
    private static final double REGULARIZATION = 0.02;
    private static final int MAX_ITERATIONS = 100;
    private static final double CONVERGENCE_THRESHOLD = 0.001;

    @Override
    public Page<ProductDto> getRecommendations(User user, Pageable pageable, boolean includeRatingWeight) {
        // Get all users and products
        List<User> allUsers = orderRepository.findAllUsersWithOrders();
        List<Product> allProducts = productRepository.findAll();

        // Build interaction matrix
        RealMatrix interactionMatrix = buildInteractionMatrix(allUsers, allProducts, includeRatingWeight);

        // Get user index
        int userIndex = allUsers.indexOf(user);
        if (userIndex == -1) {
            return Page.empty(pageable);
        }

        // Perform matrix factorization
        MatrixFactorizationResult factorization = performFactorization(interactionMatrix);

        // Calculate recommendations
        List<ScoredProduct> recommendations = calculateRecommendations(
                user,
                userIndex,
                allProducts,
                factorization,
                includeRatingWeight
        );

        // Apply pagination
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

    private RealMatrix buildInteractionMatrix(List<User> users, List<Product> products, boolean includeRatingWeight) {
        int userCount = users.size();
        int productCount = products.size();
        double[][] matrix = new double[userCount][productCount];

        for (int i = 0; i < userCount; i++) {
            User user = users.get(i);
            Set<Long> purchasedProductIds = orderRepository.findProductsPurchasedByUser(user.getId())
                    .stream()
                    .map(Product::getId)
                    .collect(Collectors.toSet());

            for (int j = 0; j < productCount; j++) {
                Product product = products.get(j);
                if (purchasedProductIds.contains(product.getId())) {
                    if (includeRatingWeight) {
                        Optional<ProductRating> rating = ratingRepository.findByUserIdAndProductId(user.getId(), product.getId());
                        matrix[i][j] = rating.map(r -> r.getRating() / 5.0).orElse(1.0);
                    } else {
                        matrix[i][j] = 1.0;
                    }
                }
            }
        }

        return MatrixUtils.createRealMatrix(matrix);
    }

    @Data
    @AllArgsConstructor
    private static class MatrixFactorizationResult {
        private RealMatrix userFactors;
        private RealMatrix itemFactors;
    }

    private MatrixFactorizationResult performFactorization(RealMatrix R) {
        int users = R.getRowDimension();
        int items = R.getColumnDimension();

        // Initialize factor matrices with small random values
        Random random = new Random(42); // Fixed seed for reproducibility
        RealMatrix P = MatrixUtils.createRealMatrix(users, LATENT_FACTORS);
        RealMatrix Q = MatrixUtils.createRealMatrix(items, LATENT_FACTORS);

        for (int i = 0; i < users; i++) {
            for (int j = 0; j < LATENT_FACTORS; j++) {
                P.setEntry(i, j, random.nextDouble() * 0.1);
            }
        }
        for (int i = 0; i < items; i++) {
            for (int j = 0; j < LATENT_FACTORS; j++) {
                Q.setEntry(i, j, random.nextDouble() * 0.1);
            }
        }

        // Gradient descent
        double prevError = Double.MAX_VALUE;
        for (int iter = 0; iter < MAX_ITERATIONS; iter++) {
            double error = 0;
            for (int i = 0; i < users; i++) {
                for (int j = 0; j < items; j++) {
                    if (R.getEntry(i, j) > 0) {
                        double rij = R.getEntry(i, j);
                        double prediction = calculatePrediction(P, Q, i, j);
                        double eij = rij - prediction;
                        error += eij * eij;

                        // Update latent factors
                        for (int k = 0; k < LATENT_FACTORS; k++) {
                            double pik = P.getEntry(i, k);
                            double qkj = Q.getEntry(j, k);

                            P.setEntry(i, k, pik + LEARNING_RATE * (2 * eij * qkj - REGULARIZATION * pik));
                            Q.setEntry(j, k, qkj + LEARNING_RATE * (2 * eij * pik - REGULARIZATION * qkj));
                        }
                    }
                }
            }

            // Check convergence
            error = Math.sqrt(error / (users * items));
            if (Math.abs(error - prevError) < CONVERGENCE_THRESHOLD) {
                log.debug("Converged after {} iterations", iter + 1);
                break;
            }
            prevError = error;
        }

        return new MatrixFactorizationResult(P, Q);
    }

    private double calculatePrediction(RealMatrix P, RealMatrix Q, int userIndex, int itemIndex) {
        double sum = 0;
        for (int k = 0; k < LATENT_FACTORS; k++) {
            sum += P.getEntry(userIndex, k) * Q.getEntry(itemIndex, k);
        }
        return sum;
    }

    private List<ScoredProduct> calculateRecommendations(
            User user,
            int userIndex,
            List<Product> allProducts,
            MatrixFactorizationResult factorization,
            boolean includeRatingWeight) {

        Set<Long> purchasedProductIds = orderRepository.findProductsPurchasedByUser(user.getId())
                .stream()
                .map(Product::getId)
                .collect(Collectors.toSet());

        List<ScoredProduct> recommendations = new ArrayList<>();

        for (int i = 0; i < allProducts.size(); i++) {
            Product product = allProducts.get(i);
            if (!purchasedProductIds.contains(product.getId())) {
                double score = calculatePrediction(
                        factorization.getUserFactors(),
                        factorization.getItemFactors(),
                        userIndex,
                        i
                );

                if (includeRatingWeight) {
                    Double avgRating = ratingRepository.getAverageRating(product.getId());
                    Long ratingCount = ratingRepository.getRatingCount(product.getId());

                    if (avgRating != null && ratingCount != null) {
                        double ratingScore = (avgRating / 5.0) * Math.min(ratingCount / 10.0, 1.0);
                        score = score * 0.7 + ratingScore * 0.3;
                    }
                }

                recommendations.add(new ScoredProduct(product, score));
            }
        }

        Collections.sort(recommendations);
        return recommendations;
    }
}


