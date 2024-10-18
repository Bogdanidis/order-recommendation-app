//package com.example.order_app.service.recommendation;
//
//import com.example.order_app.repository.OrderRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.Map;
//
//@Service
//public class FlexibleRecommendationService implements IRecommendationService {
//
//    private final Map<String, RecommendationStrategy> strategies;
//    private final OrderRepository orderRepository;
//
//    public FlexibleRecommendationService(UserBasedCFStrategy userBasedCF,
//                                         ItemBasedCFStrategy itemBasedCF,
//                                         RatingMatrixFactorizationStrategy matrixFactorization,
//                                         OrderRepository orderRepository) {
//        this.strategies = Map.of(
//                "userBasedCF", userBasedCF,
//                "itemBasedCF", itemBasedCF,
//                "matrixFactorization", matrixFactorization
//        );
//        this.orderRepository = orderRepository;
//    }
//
//
//
//    /**
//     * Get recommendations for a user, using a hybrid approach based on user activity.
//     * Results are cached for better performance.
//     *
//     * @param user The user to get recommendations for
//     * @param numRecommendations The number of recommendations to return
//     * @return A list of recommended ProductDto objects
//     */
//    @Override
//    //@Cacheable(value = "recommendations", key = "#user.id")
//    public List<ProductDto> getRecommendationsForUser(User user, int numRecommendations) {
//        long userOrderCount = orderRepository.countByUser(user);
//
//        if (userOrderCount == 0) {
//            // For new users with no orders, use matrix factorization
//            return strategies.get("matrixFactorization").getRecommendations(user, numRecommendations);
//        } else if (userOrderCount < 5) {
//            // For users with few orders, use item-based CF
//            return strategies.get("itemBasedCF").getRecommendations(user, numRecommendations);
//        } else if (userOrderCount > 20) {
//            // For users with many orders, use user-based CF
//            return strategies.get("userBasedCF").getRecommendations(user, numRecommendations);
//        } else {
//            // For users with moderate activity, blend recommendations
//            List<ProductDto> matrixRecs = strategies.get("matrixFactorization").getRecommendations(user, numRecommendations);
//            List<ProductDto> userBasedRecs = strategies.get("userBasedCF").getRecommendations(user, numRecommendations);
//            return blendRecommendations(matrixRecs, userBasedRecs, numRecommendations);
//        }
//    }
//
//
//    /**
//     * Blend recommendations from two different strategies.
//     * This method alternates between the two lists and sorts by predicted rating.
//     *
//     * @param list1 First list of recommendations
//     * @param list2 Second list of recommendations
//     * @param numRecommendations The number of recommendations to return
//     * @return A blended list of recommendations
//     */
//    private List<ProductDto> blendRecommendations(List<ProductDto> list1, List<ProductDto> list2, int numRecommendations) {
//        List<ProductDto> blendedList = new ArrayList<>();
//        int i = 0, j = 0;
//
//        while (blendedList.size() < numRecommendations * 2 && (i < list1.size() || j < list2.size())) {
//            if (i < list1.size()) {
//                blendedList.add(list1.get(i++));
//            }
//            if (j < list2.size()) {
//                blendedList.add(list2.get(j++));
//            }
//        }
//
//        // Sort the blended list by predicted rating and return the top recommendations
//        return blendedList.stream()
//                .sorted(Comparator.comparingDouble(ProductDto::getPredictedRating).reversed())
//                .distinct()
//                .limit(numRecommendations)
//                .collect(Collectors.toList());
//    }
//}
