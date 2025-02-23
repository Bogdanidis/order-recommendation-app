package com.example.order_app.controller.rest;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.dto.ProductRatingDto;
import com.example.order_app.dto.RatingStatisticsDto;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Product;
import com.example.order_app.model.User;
import com.example.order_app.request.AddProductRequest;
import com.example.order_app.request.UpdateProductRequest;
import com.example.order_app.response.RestResponse;
import com.example.order_app.response.PageMetadata;
import com.example.order_app.service.product.IProductService;
import com.example.order_app.service.rating.IProductRatingService;
import com.example.order_app.service.user.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/products")
@Tag(name = "Products", description = "Endpoints for managing products")
public class ProductRestController {
    private final IProductService productService;
    private final IProductRatingService ratingService;
    private final IUserService userService;

    /**
     * Search products with optional filters
     */
    @GetMapping("/search")
    @Operation(summary = "Search products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products found")
    })
    public ResponseEntity<RestResponse<?>> searchProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) String brandName,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String category) {

        Page<ProductDto> productPage = productService.searchProducts(
                PageRequest.of(page, size), brandName, productName, category);

        return ResponseEntity.ok(new RestResponse<>("Products found", productPage));
    }

    /**
     * Get product details including ratings
     */
    @GetMapping("/{productId}")
    @Operation(summary = "Get product details with ratings")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product details retrieved"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<RestResponse<?>> getProductDetails(
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
            var response = new RestResponse<>("Product found", productDto);
            response.setPage(new PageMetadata(ratingStats.getContent()));

            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Add a new product (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Add new product", description = "Admin only")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid product data")
    })
    public ResponseEntity<RestResponse<?>> addProduct(
            @Valid @RequestBody AddProductRequest request) {
        try {
            Product product = productService.addProduct(request);
            ProductDto productDto = productService.convertToDto(product);
            return ResponseEntity.ok(new RestResponse<>("Product created successfully", productDto));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Update an existing product (Admin only)
     */
    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update product", description = "Admin only")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<RestResponse<?>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request) {
        try {
            Product product = productService.updateProduct(request, productId);
            ProductDto productDto = productService.convertToDto(product);
            return ResponseEntity.ok(new RestResponse<>("Product updated successfully", productDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Delete a product (Admin only)
     */
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete product", description = "Admin only")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<RestResponse<?>> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProductById(productId);
            return ResponseEntity.ok(new RestResponse<>("Product deactivated successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Rate a product (Authenticated users only)
     */
    @PostMapping("/{productId}/rate")
    @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
    @Operation(summary = "Rate product")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rating submitted successfully"),
            @ApiResponse(responseCode = "403", description = "User has not purchased this product")
    })
    public ResponseEntity<RestResponse<?>> rateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductRatingDto ratingDto) {
        try {
            User user = userService.getAuthenticatedUser();

            // Verify user has purchased the product
            if (!productService.hasUserPurchasedProduct(user.getId(), productId)) {
                return ResponseEntity.status(FORBIDDEN)
                        .body(new RestResponse<>("You can only rate products you have purchased", null));
            }

            var rating = ratingService.addRating(productId, ratingDto, user);
            return ResponseEntity.ok(new RestResponse<>("Rating submitted successfully", rating));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }

    /**
     * Get product ratings
     */
    @GetMapping("/{productId}/ratings")
    @Operation(summary = "Get product ratings")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product ratings retrieved"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<RestResponse<?>> getProductRatings(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            Page<ProductRatingDto> ratings = ratingService.getProductRatings(
                    productId, PageRequest.of(page, size));
            return ResponseEntity.ok(new RestResponse<>("Ratings retrieved successfully", ratings));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new RestResponse<>(e.getMessage(), null));
        }
    }
}
