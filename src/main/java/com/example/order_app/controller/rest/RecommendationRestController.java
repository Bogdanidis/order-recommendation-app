package com.example.order_app.controller.rest;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.User;
import com.example.order_app.response.ApiResponse;
import com.example.order_app.service.recommendation.IRecommendationService;
import com.example.order_app.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/recommendations")
public class RecommendationRestController {
    private final IRecommendationService recommendationService;
    private final IUserService userService;

    /**
     * Get personalized product recommendations for the authenticated user
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse<ProductDto>> getRecommendations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        try {
            User user = userService.getAuthenticatedUser();

            Page<ProductDto> recommendations = recommendationService
                    .getRecommendationsForUser(user, PageRequest.of(page, size));

            return ResponseEntity.ok(new ApiResponse<>("Recommendations retrieved successfully",
                    recommendations));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Get recommendations for a specific user (Admin only)
     */
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ProductDto>> getUserRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        try {
            User user = userService.getUserById(userId);

            Page<ProductDto> recommendations = recommendationService
                    .getRecommendationsForUser(user, PageRequest.of(page, size));

            return ResponseEntity.ok(new ApiResponse<>("Recommendations retrieved successfully",
                    recommendations));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }
}