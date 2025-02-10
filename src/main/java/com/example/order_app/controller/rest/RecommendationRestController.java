package com.example.order_app.controller.rest;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.User;
import com.example.order_app.response.RestResponse;
import com.example.order_app.service.recommendation.IRecommendationService;
import com.example.order_app.service.user.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Recommendations", description = "Endpoints for product recommendations")
public class RecommendationRestController {
    private final IRecommendationService recommendationService;
    private final IUserService userService;

    /**
     * Get personalized product recommendations for the authenticated user
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get personalized recommendations")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<RestResponse<ProductDto>> getRecommendations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        try {
            User user = userService.getAuthenticatedUser();

            Page<ProductDto> recommendations = recommendationService
                    .getRecommendationsForUser(user, PageRequest.of(page, size));

            return ResponseEntity.ok(new RestResponse<>("Recommendations retrieved successfully",
                    recommendations));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Get recommendations for a specific user (Admin only)
     */
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get user recommendations", description = "Admin only")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<RestResponse<ProductDto>> getUserRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        try {
            User user = userService.getUserById(userId);

            Page<ProductDto> recommendations = recommendationService
                    .getRecommendationsForUser(user, PageRequest.of(page, size));

            return ResponseEntity.ok(new RestResponse<>("Recommendations retrieved successfully",
                    recommendations));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }
}