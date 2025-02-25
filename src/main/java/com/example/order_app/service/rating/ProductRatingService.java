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
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.order_app.exception.UnauthorizedOperationException;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ProductRatingService implements IProductRatingService {

    private final ProductRatingRepository ratingRepository;
    private final ProductRepository productRepository;
    private final IProductService productService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    @Override
    @Transactional
    public ProductRating addRating(Long productId, ProductRatingDto ratingDto, User user) {

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
        // Also add the rating to the product's list
        product.getRatings().add(rating);
        // Also add the rating to the user's rating list
        user.getRatings().add(rating);


        ProductRating savedRating = ratingRepository.save(rating);

        updateProductRatingCache(product);

        return savedRating;
    }

    @Override
    public Page<ProductRatingDto> getProductRatings(Long productId, Pageable pageable) {
        return ratingRepository.findByProductId(productId, pageable)
                .map(this::convertToDto);
    }

    @Cacheable(value = "productRatings", key = "#productId + '_' + #pageable.pageNumber")
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
    @CacheEvict(value = {"productRatings", "products"}, allEntries = true)
    public void updateProductRatingCache(Product product) {
        double avgRating = ratingRepository.calculateAverageRating(product.getId());
        long ratingCount = ratingRepository.countByProductId(product.getId());

        product.setAverageRating(avgRating);
        product.setRatingCount(ratingCount);

        productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteRating(Long ratingId, Long userId) {
        ProductRating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found"));

        if (!rating.getUser().getId().equals(userId)) {
            throw new UnauthorizedOperationException("Cannot delete other user's ratings");
        }

        ratingRepository.findById(ratingId)
                .ifPresentOrElse(
                        product -> ratingRepository.softDelete(ratingId, LocalDateTime.now()),
                        () -> { throw new ResourceNotFoundException("Product Rating not found!"); }
                );
        updateProductRatingCache(rating.getProduct());
    }
}