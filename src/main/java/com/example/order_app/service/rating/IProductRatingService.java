package com.example.order_app.service.rating;

import com.example.order_app.dto.ProductRatingDto;
import com.example.order_app.dto.RatingStatisticsDto;
import com.example.order_app.model.Product;
import com.example.order_app.model.ProductRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductRatingService {

    ProductRating addRating(Long productId, ProductRatingDto ratingDto);


    Page<ProductRatingDto> getProductRatings(Long productId, Pageable pageable);

    RatingStatisticsDto getProductRatingStatistics(Long productId, Pageable pageable);

    boolean hasUserRatedProduct(Long userId, Long productId);

    ProductRatingDto convertToDto(ProductRating rating);

    void updateProductRatingCache(Product product);
}