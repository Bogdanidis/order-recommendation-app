package com.example.order_app.service.recommendation;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.model.Product;
import com.example.order_app.model.User;
import com.example.order_app.repository.OrderRepository;
import com.example.order_app.repository.ProductRatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService implements IRecommendationService {
    private final Map<String, RecommendationStrategy> strategies;
    private final OrderRepository orderRepository;
    private final ProductRatingRepository ratingRepository;

    @Cacheable(value = "productRecommendations",
            key = "#user.id + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    @Override
    public Page<ProductDto> getRecommendationsForUser(User user, Pageable pageable) {
        RecommendationType recommendationType = determineRecommendationType(user);
        return getRecommendations(user, recommendationType, pageable);
    }

    private RecommendationType determineRecommendationType(User user) {
        long orderCount = orderRepository.countByUserId(user.getId());
        boolean hasRatings = !ratingRepository.findByUserId(user.getId()).isEmpty();

        if (orderCount == 0) {
            return hasRatings ? RecommendationType.MATRIX_FACTORIZATION : RecommendationType.CONTENT_BASED;
        } else if (orderCount < 5) {
            return hasRatings ? RecommendationType.ITEM_BASED : RecommendationType.CONTENT_BASED;
        } else {
            return hasRatings ? RecommendationType.USER_BASED : RecommendationType.ITEM_BASED;
        }
    }

    private Page<ProductDto> getRecommendations(User user, RecommendationType type, Pageable pageable) {
        String strategyName = switch (type) {
            case USER_BASED -> "userBasedCF";
            case ITEM_BASED -> "itemBasedCF";
            case CONTENT_BASED -> "contentBased";
            case MATRIX_FACTORIZATION -> "matrixFactorization";
        };

        return strategies.get(strategyName)
                .getRecommendations(user, pageable, true);
    }

    private enum RecommendationType {
        USER_BASED,
        ITEM_BASED,
        CONTENT_BASED,
        MATRIX_FACTORIZATION
    }
}

