package com.example.order_app.controller;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.dto.ProductRatingDto;
import com.example.order_app.dto.RatingStatisticsDto;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Product;
import com.example.order_app.model.User;
import com.example.order_app.request.AddProductRequest;
import com.example.order_app.request.SearchRequest;
import com.example.order_app.request.UpdateProductRequest;
import com.example.order_app.service.category.ICategoryService;
import com.example.order_app.service.product.IProductService;
import com.example.order_app.service.rating.IProductRatingService;
import com.example.order_app.service.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final IProductService productService;
    private final ICategoryService categoryService;
    private final IUserService userService;
    private final IProductRatingService productRatingService;
    private static final int RATINGS_PER_PAGE = 5;


    /**
     * Displays a list of all products with optional search functionality.
     *
     * @param page The page number (default: 0)
     * @param size The number of items per page (default: 9)
     * @param request The search request containing filter criteria
     * @param model Spring MVC Model
     * @return The name of the product list view
     */
    @GetMapping
    public String getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @ModelAttribute SearchRequest request,
            Model model) {

        // Initialize a default request if not provided
        if (request == null) {
            request = new SearchRequest();
        }

        // Set default sorting if not specified
        if (request.getSortBy() == null || request.getSortBy().isEmpty()) {
            request.setSortBy("name");
        }
        if (request.getSortDirection() == null || request.getSortDirection().isEmpty()) {
            request.setSortDirection("asc");
        }

        // Get the search results
        Page<ProductDto> productPage = productService.searchProducts(
                request, PageRequest.of(page, size));

        // Add data to model
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("request", request);
        model.addAttribute("size", size);

        // Add categories for the dropdown
        model.addAttribute("categories", categoryService.getAllCategories());

        return "product/list";
    }
    /**
     * Displays a list of products based on search criteria.
     *
     * @param request The search request with multiple criteria
     * @param page The page number (default: 0)
     * @param size The number of items per page (default: 9)
     * @param model Spring MVC Model
     * @return The name of the product list view
     */
    @GetMapping("/search")
    public String search(
            @ModelAttribute SearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {
        try {
            // Clean up request parameters if necessary
            if (request.getCategoryName() != null && request.getCategoryName().isEmpty()) {
                request.setCategoryName(null);
            }

            log.debug("Search request: name={}, brand={}, category={}, " +
                            "price={}~{}, stock={}, inStock={}, sort={}({})",
                    request.getName(), request.getBrand(), request.getCategoryName(),
                    request.getMinPrice(), request.getMaxPrice(), request.getMinStock(),
                    request.getInStock(), request.getSortBy(), request.getSortDirection());

            // Ensure default sort values if not present
            if (request.getSortBy() == null || request.getSortBy().isEmpty()) {
                request.setSortBy("name");
            }

            if (request.getSortDirection() == null || request.getSortDirection().isEmpty()) {
                request.setSortDirection("asc");
            }

            // Get search results
            Page<ProductDto> productPage = productService.searchProducts(
                    request, PageRequest.of(page, size));

            // Add data to model
            model.addAttribute("products", productPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", productPage.getTotalPages());
            model.addAttribute("totalItems", productPage.getTotalElements());
            model.addAttribute("request", request);
            model.addAttribute("size", size);
            model.addAttribute("categories", categoryService.getAllCategories());

            return "product/list";
        } catch (Exception e) {
            log.error("Error in search: ", e);
            model.addAttribute("error", "Error in search: " + e.getMessage());
            return "redirect:/products";
        }
    }

    /**
     * Displays product details along with ratings and reviews
     * @param productId The ID of the product to display
     * @param page The page number for ratings pagination
     * @param model Spring MVC Model
     * @return The product details view
     */
    @GetMapping("/{productId}")
    public String getProductById(@PathVariable Long productId,
                                 @RequestParam(defaultValue = "0") int page,
                                 Model model,
                                 Authentication authentication) {
        try {
            // Get product details
            Product product = productService.getProductById(productId);
            ProductDto productDto = productService.convertToDto(product);
            model.addAttribute("product", productDto);

            // Get rating statistics and paginated ratings
            Pageable pageable = PageRequest.of(page, RATINGS_PER_PAGE);
            RatingStatisticsDto ratingStats = productRatingService.getProductRatingStatistics(productId, pageable);
            model.addAttribute("ratingStats", ratingStats);

            // Add empty DTO for the rating form
            model.addAttribute("productRatingDto", new ProductRatingDto());

            // If user is authenticated, get their review and purchase status
            if (authentication != null && authentication.isAuthenticated() &&
                    authentication.getAuthorities().stream()
                            .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {

                User user = userService.getAuthenticatedUser();
                Long userId = user.getId();

                // Check if user has purchased the product
                boolean hasOrdered = productService.hasUserPurchasedProduct(userId, productId);
                model.addAttribute("hasOrdered", hasOrdered);

                // Check if user has already reviewed
                boolean hasReviewed = productRatingService.hasUserRatedProduct(userId, productId);
                model.addAttribute("hasReviewed", hasReviewed);

                // If user has reviewed, get their review
                if (hasReviewed) {
                    Optional<ProductRatingDto> userReview = ratingStats.getContent().getContent().stream()
                            .filter(rating -> rating.getUserId() != null && rating.getUserId().equals(userId))
                            .findFirst();
                    userReview.ifPresent(review -> model.addAttribute("userReview", review));
                }
            }

            return "product/details";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/products";
        }
    }

    /**
     * Handles the submission of a new product rating
     * @param productId The ID of the product being rated
     * @param productRatingDto The rating data
     * @param bindingResult Validation results
     * @param model Spring MVC Model
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect to product details
     */
    @PostMapping("/{productId}/rate")
    public String rateProduct(@PathVariable Long productId,
                              @Valid @ModelAttribute ProductRatingDto productRatingDto,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please provide a valid rating (1-5) and comment");
            return "redirect:/products/" + productId;
        }

        try {
            User user = userService.getAuthenticatedUser();
            productRatingService.addRating(productId, productRatingDto, user);
            redirectAttributes.addFlashAttribute("success", "Thank you for your review!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/products/" + productId;
    }

    /**
     * Displays the form to add a new product.
     *
     * @param model Spring MVC Model
     * @return The name of the add product view
     */
    @GetMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String showAddProductForm(Model model) {
        model.addAttribute("addProductRequest", new AddProductRequest());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "product/add";
    }

    /**
     * Handles the submission of a new product.
     *
     * @param product The product to be added
     * @param bindingResult BindingResult for form validation
     * @param model Spring MVC Model
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect URL
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addProduct(@Valid @ModelAttribute("product") AddProductRequest product,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "product/add";
        }
        try {
            Product theProduct = productService.addProduct(product);
            redirectAttributes.addFlashAttribute("success", "Product added successfully");
            return "redirect:/products/" + theProduct.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/products/add";
        }
    }


    /**
     * Displays the form to update an existing product.
     *
     * @param productId ID of the product to update
     * @param model Spring MVC Model
     * @return The name of the update product view or a redirect URL
     */
    @GetMapping("/{productId}/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String showUpdateProductForm(@PathVariable Long productId, Model model) {
        try {
            Product product = productService.getProductById(productId);
            ProductDto productDto = productService.convertToDto(product);
            UpdateProductRequest updateRequest = new UpdateProductRequest();
            // Map ProductDto to UpdateProductRequest
            BeanUtils.copyProperties(productDto, updateRequest);

            model.addAttribute("product", updateRequest);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "product/update";
        } catch (ResourceNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/products";
        }
    }

    /**
     * Handles the submission of an updated product.
     *
     * @param productId ID of the product to update
     * @param request The updated product data
     * @param bindingResult BindingResult for form validation
     * @param model Spring MVC Model
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect URL
     */
    @PutMapping("/{productId}/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String updateProduct(@PathVariable Long productId,
                                @Valid @ModelAttribute("product") UpdateProductRequest request,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "product/update";
        }
        try {
            Product theProduct = productService.updateProduct(request, productId);
            redirectAttributes.addFlashAttribute("success", "Product updated successfully");
            return "redirect:/products/" + theProduct.getId();
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/products";
        }
    }

//    /**
//     * Displays the rating page for a product with rating statistics and paginated reviews
//     * @param productId ID of the product
//     * @param page Page number for ratings pagination
//     * @param model Spring MVC Model
//     * @return The rating page view or redirect to products list
//     */
//    @GetMapping("/{productId}/rate")
//    public String showRatingPage(@PathVariable Long productId,
//                                 @RequestParam(defaultValue = "0") int page,
//                                 Model model) {
//        try {
//            // Get product details
//            Product product = productService.getProductById(productId);
//            ProductDto productDto = productService.convertToDto(product);
//            model.addAttribute("product", productDto);
//
//            // Get rating statistics and paginated ratings
//            Pageable pageable = PageRequest.of(page, RATINGS_PER_PAGE);
//            RatingStatisticsDto ratingStats = productRatingService.getProductRatingStatistics(productId, pageable);
//            model.addAttribute("ratingStats", ratingStats);
//
//            // Add empty DTO for the rating form
//            model.addAttribute("productRatingDto", new ProductRatingDto());
//
//            return "product/rate";
//        } catch (ResourceNotFoundException e) {
//            model.addAttribute("error", e.getMessage());
//            return "redirect:/products";
//        }
//    }
//
//    /**
//     * Handles the submission of a new product rating
//     * @param productId ID of the product being rated
//     * @param productRatingDto The rating data
//     * @param bindingResult Validation results
//     * @param model Spring MVC Model
//     * @param redirectAttributes RedirectAttributes for flash messages
//     * @return Redirect to rating page or back to form with errors
//     */
//    @PostMapping("/{productId}/rate")
//    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
//    public String rateProduct(@PathVariable Long productId,
//                              @Valid @ModelAttribute ProductRatingDto productRatingDto,
//                              BindingResult bindingResult,
//                              Model model,
//                              RedirectAttributes redirectAttributes) {
//        if (bindingResult.hasErrors()) {
//            // Re-populate the page with necessary data
//            try {
//                Product product = productService.getProductById(productId);
//                ProductDto productDto = productService.convertToDto(product);
//                model.addAttribute("product", productDto);
//
//                // Get fresh rating statistics
//                Pageable pageable = PageRequest.of(0, RATINGS_PER_PAGE);
//                RatingStatisticsDto ratingStats = productRatingService
//                        .getProductRatingStatistics(productId, pageable);
//                model.addAttribute("ratingStats", ratingStats);
//
//                return "product/rate";
//            } catch (ResourceNotFoundException e) {
//                redirectAttributes.addFlashAttribute("error", e.getMessage());
//                return "redirect:/products";
//            }
//        }
//
//        try {
//            productRatingService.addRating(productId, productRatingDto);
//            redirectAttributes.addFlashAttribute("success", "Thank you for your review!");
//        } catch (AlreadyExistsException e) {
//            redirectAttributes.addFlashAttribute("error", "You have already reviewed this product");
//        } catch (UnauthorizedOperationException e) {
//            redirectAttributes.addFlashAttribute("error", e.getMessage());
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error",
//                    "An error occurred while submitting your review: " + e.getMessage());
//        }
//
//        return "redirect:/products/" + productId + "/rate";
//    }

    /**
     * Handles the deletion of a product.
     *
     * @param productId ID of the product to delete
     * @param redirectAttributes RedirectAttributes for flash messages
     * @return Redirect URL
     */
    @DeleteMapping("/{productId}/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteProduct(@PathVariable Long productId, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProductById(productId);
            redirectAttributes.addFlashAttribute("success", "Product deactivated successfully");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }

}
