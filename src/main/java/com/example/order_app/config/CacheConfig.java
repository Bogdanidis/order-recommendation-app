package com.example.order_app.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.cache.CacheMetricsRegistrar;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${cache.ttl.products:3600}") // 1 hour default
    private long productCacheTtl;

    @Value("${cache.ttl.categories:7200}") // 2 hours default
    private long categoryCacheTtl;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // Set default cache spec
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(1800, TimeUnit.SECONDS)
                .maximumSize(500));

        // Register caches with custom configurations
        cacheManager.registerCustomCache("products",
                Caffeine.newBuilder()
                        .expireAfterWrite(productCacheTtl, TimeUnit.SECONDS)
                        .maximumSize(1000)
                        .recordStats()
                        .build());

        cacheManager.registerCustomCache("categories",
                Caffeine.newBuilder()
                        .expireAfterWrite(categoryCacheTtl, TimeUnit.SECONDS)
                        .maximumSize(200)
                        .build());

        cacheManager.registerCustomCache("productRecommendations",
                Caffeine.newBuilder()
                        .expireAfterWrite(1800, TimeUnit.SECONDS)
                        .maximumSize(500)
                        .build());

        cacheManager.registerCustomCache("productRatings",
                Caffeine.newBuilder()
                        .expireAfterWrite(3600, TimeUnit.SECONDS)
                        .maximumSize(1000)
                        .build());

        cacheManager.registerCustomCache("users",
                Caffeine.newBuilder()
                        .expireAfterWrite(3600, TimeUnit.SECONDS)
                        .maximumSize(500)
                        .build());

        return cacheManager;
    }
}