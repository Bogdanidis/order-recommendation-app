package com.example.order_app.service.recommendation;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.model.User;
import com.example.order_app.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FlexibleRecommendationService implements IRecommendationService {

    private final Map<String, RecommendationStrategy> strategies;
    private final OrderRepository orderRepository;

    public FlexibleRecommendationService(UserBasedCFStrategy userBasedCF,
                                         //ItemBasedCFStrategy itemBasedCF,
                                         //MatrixFactorizationStrategy matrixFactorization,
                                         OrderRepository orderRepository) {
        this.strategies = Map.of(
                "userBasedCF", userBasedCF
                //"itemBasedCF", itemBasedCF,
                //"matrixFactorization", matrixFactorization
        );
        this.orderRepository = orderRepository;
    }
//  Receive recommendations from cache for better performance
//    @Override
//    @Cacheable(value = "recommendations", key = "#user.id")
//    public List<ProductDto> getRecommendationsForUser(User user, int numRecommendations) {
//        RecommendationStrategy strategy = chooseStrategy(user);
//        return strategy.getRecommendations(user, numRecommendations);
//    }

    @Override
    public List<ProductDto> getRecommendationsForUser(User user, int numRecommendations) {
        RecommendationStrategy strategy = chooseStrategy(user);
        return strategy.getRecommendations(user, numRecommendations);
    }


//  Choose Recommendation Strategy based on userOrderCount
    private RecommendationStrategy chooseStrategy(User user) {
//        long userOrderCount = orderRepository.countByUser(user);
//
//        if (userOrderCount == 0) {
//            // For new users with no orders, use matrix factorization
//            return strategies.get("matrixFactorization");
//        } else if (userOrderCount < 5) {
//            // For users with few orders, use item-based CF
//            return strategies.get("itemBasedCF");
//        } else {
//            // For users with many orders, use user-based CF
//            return strategies.get("userBasedCF");
//        }

        return strategies.get("userBasedCF");
    }
}