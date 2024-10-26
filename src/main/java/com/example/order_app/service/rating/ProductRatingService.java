package com.example.order_app.service.rating;

import com.example.order_app.dto.ProductRatingDto;
import com.example.order_app.dto.RatingStatisticsDto;
import com.example.order_app.exception.AlreadyExistsException;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Product;
import com.example.order_app.model.ProductRating;
import com.example.order_app.model.User;
import com.example.order_app.repository.ProductRatingRepository;
import com.example.order_app.repository.ProductRepository;
import com.example.order_app.service.product.IProductService;
import com.example.order_app.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.order_app.exception.UnauthorizedOperationException;


import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ProductRatingService implements IProductRatingService {

    private final ProductRatingRepository ratingRepository;
    private final ProductRepository productRepository;
    private final IUserService userService;
    private final IProductService productService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    @Override
    @Transactional
    public ProductRating addRating(Long productId, ProductRatingDto ratingDto) {
        User user = userService.getAuthenticatedUser();

        // Check if user has already rated this product
        if (hasUserRatedProduct(user.getId(), productId)) {
            throw new AlreadyExistsException("You have already rated this product");
        }

        // Check if user has purchased the product
        if (!productService.hasUserPurchasedProduct(user.getId(), productId)) {
            throw new UnauthorizedOperationException("You can only rate products you have purchased");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        ProductRating rating = new ProductRating();
        rating.setRating(ratingDto.getRating());
        rating.setComment(ratingDto.getComment());
        rating.setProduct(product);
        rating.setUser(user);

        ProductRating savedRating = ratingRepository.save(rating);
        updateProductRatingCache(product);

        return savedRating;
    }

    @Override
    public Page<ProductRatingDto> getProductRatings(Long productId, Pageable pageable) {
        return ratingRepository.findByProductId(productId, pageable)
                .map(this::convertToDto);
    }

    @Override
    public RatingStatisticsDto getProductRatingStatistics(Long productId, Pageable pageable) {
        Page<ProductRatingDto> ratings = getProductRatings(productId, pageable);
        return new RatingStatisticsDto(ratings);
    }

    @Override
    public boolean hasUserRatedProduct(Long userId, Long productId) {
        return ratingRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    public ProductRatingDto convertToDto(ProductRating rating) {
        ProductRatingDto dto = new ProductRatingDto();
        dto.setId(rating.getId());
        dto.setRating(rating.getRating());
        dto.setComment(rating.getComment());
        dto.setUserFullName(rating.getUser().getFirstName() + " " + rating.getUser().getLastName());
        dto.setUserId(rating.getUser().getId());
        dto.setFormattedDate(rating.getCreatedAt().format(FORMATTER));
        return dto;
    }

    @Override
    public void updateProductRatingCache(Product product) {
        double avgRating = ratingRepository.calculateAverageRating(product.getId());
        long ratingCount = ratingRepository.countByProductId(product.getId());

        product.setAverageRating(avgRating);
        product.setRatingCount(ratingCount);

        productRepository.save(product);
    }
}