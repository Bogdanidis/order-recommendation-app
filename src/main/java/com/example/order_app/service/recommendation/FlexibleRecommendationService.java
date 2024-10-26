package com.example.order_app.service.recommendation;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.model.Cart;
import com.example.order_app.model.CartItem;
import com.example.order_app.model.Product;
import com.example.order_app.model.User;
import com.example.order_app.repository.CartRepository;
import com.example.order_app.repository.OrderRepository;
import com.example.order_app.service.product.IProductService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlexibleRecommendationService implements IRecommendationService {

    private final Map<String, RecommendationStrategy> strategies;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final IProductService productService;

    public FlexibleRecommendationService(UserBasedCFStrategy userBasedCF,
                                         ItemBasedCFStrategy itemBasedCF,
                                         MatrixFactorizationStrategy matrixFactorization,
                                         ContentBasedStrategy contentBased,
                                         OrderRepository orderRepository,
                                         CartRepository cartRepository,
                                         IProductService productService) {
        this.strategies = Map.of(
                "userBasedCF", userBasedCF,
                "itemBasedCF", itemBasedCF,
                "matrixFactorization", matrixFactorization,
                "contentBased", contentBased
        );
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productService = productService;
    }

    /**
     * Get recommendations for a user, using a hybrid approach based on user activity.
     *
     * @param user               The user to get recommendations for
     * @param numRecommendations The number of recommendations to return
     * @return A list of recommended ProductDto objects
     */
    @Override
    public List<ProductDto> getRecommendationsForUser(User user, int numRecommendations) {
        long userOrderCount = orderRepository.countByUserId(user.getId());
        Cart userCart = cartRepository.findByUserId(user.getId());

        List<ProductDto> recommendations = new ArrayList<>();

        // For users with a non-empty cart, prioritize item-based recommendations
        if (userCart != null && !userCart.getItems().isEmpty()) {
            List<Product> cartProducts = userCart.getItems().stream()
                    .map(CartItem::getProduct)
                    .collect(Collectors.toList());

            recommendations.addAll(((ItemBasedCFStrategy) strategies.get("itemBasedCF"))
                    .getCartBasedRecommendations(cartProducts, numRecommendations / 3));
        }

        // Add content-based recommendations
        recommendations.addAll(strategies.get("contentBased")
                .getRecommendations(user, numRecommendations / 3));

        // Fill remaining recommendations based on user history
        int remainingRecommendations = numRecommendations - recommendations.size();
        if (remainingRecommendations > 0) {
            if (userOrderCount == 0) {
                // For new users, use matrix factorization
                recommendations.addAll(strategies.get("matrixFactorization")
                        .getRecommendations(user, remainingRecommendations));
            } else if (userOrderCount < 5) {
                // For users with few orders, use item-based CF
                recommendations.addAll(strategies.get("itemBasedCF")
                        .getRecommendations(user, remainingRecommendations));
            } else {
                // For users with more orders, use a blend of user-based and item-based CF
                int userBasedCount = remainingRecommendations / 2;
                int itemBasedCount = remainingRecommendations - userBasedCount;
                recommendations.addAll(strategies.get("userBasedCF")
                        .getRecommendations(user, userBasedCount));
                recommendations.addAll(strategies.get("itemBasedCF")
                        .getRecommendations(user, itemBasedCount));
            }
        }

        // Ensure no duplicate recommendations and limit to requested number
        return recommendations.stream()
                .distinct()
                .limit(numRecommendations)
                .collect(Collectors.toList());
    }
}
