package com.example.order_app.controller.rest;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.dto.ProductRatingDto;
import com.example.order_app.dto.RatingStatisticsDto;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Product;
import com.example.order_app.model.User;
import com.example.order_app.request.AddProductRequest;
import com.example.order_app.request.UpdateProductRequest;
import com.example.order_app.response.ApiResponse;
import com.example.order_app.response.PageMetadata;
import com.example.order_app.service.product.IProductService;
import com.example.order_app.service.rating.IProductRatingService;
import com.example.order_app.service.user.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/products")
public class ProductRestController {
    private final IProductService productService;
    private final IProductRatingService ratingService;
    private final IUserService userService;

    /**
     * Search products with optional filters
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<?>> searchProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) String brandName,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String category) {

        Page<ProductDto> productPage = productService.searchProducts(
                PageRequest.of(page, size), brandName, productName, category);

        return ResponseEntity.ok(new ApiResponse<>("Products found", productPage));
    }

    /**
     * Get product details including ratings
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<?>> getProductDetails(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int ratingsPage,
            @RequestParam(defaultValue = "5") int ratingsSize) {

        try {
            Product product = productService.getProductById(productId);
            ProductDto productDto = productService.convertToDto(product);

            // Get rating statistics
            RatingStatisticsDto ratingStats = ratingService.getProductRatingStatistics(
                    productId, PageRequest.of(ratingsPage, ratingsSize));

            // Combine product and rating data
            var response = new ApiResponse<>("Product found", productDto);
            response.setPage(new PageMetadata(ratingStats.getContent()));

            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Add a new product (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<?>> addProduct(
            @Valid @RequestBody AddProductRequest request) {
        try {
            Product product = productService.addProduct(request);
            ProductDto productDto = productService.convertToDto(product);
            return ResponseEntity.ok(new ApiResponse<>("Product created successfully", productDto));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Update an existing product (Admin only)
     */
    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request) {
        try {
            Product product = productService.updateProduct(request, productId);
            ProductDto productDto = productService.convertToDto(product);
            return ResponseEntity.ok(new ApiResponse<>("Product updated successfully", productDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Delete a product (Admin only)
     */
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<?>> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProductById(productId);
            return ResponseEntity.ok(new ApiResponse<>("Product deleted successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Rate a product (Authenticated users only)
     */
    @PostMapping("/{productId}/rate")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse<?>> rateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductRatingDto ratingDto) {
        try {
            User user = userService.getAuthenticatedUser();

            // Verify user has purchased the product
            if (!productService.hasUserPurchasedProduct(user.getId(), productId)) {
                return ResponseEntity.status(FORBIDDEN)
                        .body(new ApiResponse<>("You can only rate products you have purchased", null));
            }

            var rating = ratingService.addRating(productId, ratingDto, user);
            return ResponseEntity.ok(new ApiResponse<>("Rating submitted successfully", rating));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Get product ratings
     */
    @GetMapping("/{productId}/ratings")
    public ResponseEntity<ApiResponse<?>> getProductRatings(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            Page<ProductRatingDto> ratings = ratingService.getProductRatings(
                    productId, PageRequest.of(page, size));
            return ResponseEntity.ok(new ApiResponse<>("Ratings retrieved successfully", ratings));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), null));
        }
    }
}
