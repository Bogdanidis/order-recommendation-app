package com.example.order_app.controller;


import com.example.order_app.dto.ProductDto;
import com.example.order_app.dto.UserDto;
import com.example.order_app.model.Category;
import com.example.order_app.model.Product;
import com.example.order_app.model.User;
import com.example.order_app.service.cart.ICartService;
import com.example.order_app.service.category.ICategoryService;
import com.example.order_app.service.order.IOrderService;
import com.example.order_app.service.product.IProductService;
import com.example.order_app.service.recommendation.IRecommendationService;
import com.example.order_app.service.user.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class HomeController {

    private final IProductService productService;
    private final ICategoryService categoryService;
    private final IOrderService orderService;
    private final IUserService userService;
    private final IRecommendationService recommendationService;
    private final ApplicationContext applicationContext;



    /**
     * Redirects the root URL to the home page.
     *
     * @return Redirect URL to the home page
     */
    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }

    /**
     * Displays the home page with products, categories, and user-specific content.
     *
     * @param authentication Spring Security Authentication object
     * @param model Spring MVC Model
     * @param productPage Page number for product pagination
     * @param categoryPage Page number for category pagination
     * @param size Number of items per page
     * @return The name of the home view
     */
    @GetMapping("/home")
    public String home(Authentication authentication, Model model,
                       @RequestParam(defaultValue = "0") int productPage,
                       @RequestParam(defaultValue = "0") int categoryPage,
                       @RequestParam(defaultValue = "0") int recommendationPage) {
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        }

        // Load paginated products for the main section
        Page<Product> productPageResult = productService.getAllProducts(PageRequest.of(productPage, 6));
        model.addAttribute("products", productService.getConvertedProducts(productPageResult.getContent()));
        model.addAttribute("currentProductPage", productPage);
        model.addAttribute("totalProductPages", productPageResult.getTotalPages());

        // Load paginated categories
        Page<Category> categoryPageResult = categoryService.getAllCategoriesPaginated(PageRequest.of(categoryPage, 8)); // 8 categories per page
        model.addAttribute("categories", categoryPageResult);
        model.addAttribute("currentCategoryPage", categoryPage);
        model.addAttribute("totalCategoryPages", categoryPageResult.getTotalPages());

        // Add user/recommendations to model if authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.getAuthenticatedUser();
            UserDto userDto = userService.convertUserToDto(user);
            model.addAttribute("user", userDto);

            Page<ProductDto> recommendationPageResult = recommendationService.getRecommendationsForUser(user,PageRequest.of(recommendationPage, 3));
            model.addAttribute("recommendations", recommendationPageResult);
            model.addAttribute("currentRecommendationPage", recommendationPage);
            model.addAttribute("totalRecommendationPages", recommendationPageResult.getTotalPages());

        }

        return "home";
    }

    /**
     * Displays the admin dashboard with key metrics.
     *
     * @param model Spring MVC Model
     * @return The name of the dashboard view
     */
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String adminDashboard(Model model) {
        model.addAttribute("totalUsers", userService.countUsers());
        model.addAttribute("totalProducts", productService.countProducts());
        model.addAttribute("todayOrders", orderService.countTodaysOrders());
        model.addAttribute("todayRevenue", orderService.getTodaysRevenue());
        return "dashboard";
    }
}
