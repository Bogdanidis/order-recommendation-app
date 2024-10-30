package com.example.order_app.controller;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.dto.UserDto;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Cart;
import com.example.order_app.model.User;
import com.example.order_app.service.recommendation.IRecommendationService;
import com.example.order_app.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final IRecommendationService recommendationService;
    private final IUserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public String getRecommendations(
            @RequestParam(defaultValue = "0") int recommendationPage,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userService.getAuthenticatedUser();
            Page<ProductDto> recommendationPageResult = recommendationService.getRecommendationsForUser(user,PageRequest.of(recommendationPage, 6));
            model.addAttribute("recommendations", recommendationPageResult);
            model.addAttribute("currentRecommendationPage", recommendationPage);
            model.addAttribute("totalRecommendationPages", recommendationPageResult.getTotalPages());

            return "recommendation/list";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/home";
        }
    }
}
