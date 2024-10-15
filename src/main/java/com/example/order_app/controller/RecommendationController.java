package com.example.order_app.controller;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.model.User;
import com.example.order_app.service.recommendation.IRecommendationService;
import com.example.order_app.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/recommendations")
public class RecommendationController {
    private final IRecommendationService recommendationService;
    private final IUserService userService;

    /**
     * Displays product recommendations for the authenticated user.
     *
     * @param model Spring MVC Model
     * @return The name of the recommendations view
     */
    @GetMapping()
    @PreAuthorize("hasRole('ROLE_USER')")
    public String getRecommendations(Model model) {
        User user = userService.getAuthenticatedUser();
        List<ProductDto> recommendations = recommendationService.getRecommendationsForUser(user, 6);
        model.addAttribute("recommendations", recommendations);
        return "recommendation/list";
    }
}
