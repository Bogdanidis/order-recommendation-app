//package com.example.order_app.service.recommendation;
//
//import com.example.order_app.model.User;
//import com.example.order_app.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.cache.CacheManager;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class RecommendationRefreshService {
//
//    private final UserRepository userRepository;
//    private final IRecommendationService recommendationService;
//    private final CacheManager cacheManager;
//
//    @Scheduled(cron = "0 0 2 * * ?") // Run at 2 AM every day
//    public void refreshAllRecommendations() {
//        userRepository.findAll().forEach(this::refreshRecommendationsForUser);
//    }
//
//    private void refreshRecommendationsForUser(User user) {
//        // Evict the cache for this user
//        cacheManager.getCache("recommendations").evict(user.getId());
//
//        // Regenerate recommendations
//        recommendationService.getRecommendationsForUser(user, 10);
//    }
//}