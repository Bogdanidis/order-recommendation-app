package com.example.order_app.util.builder;


import com.example.order_app.model.Category;
import com.example.order_app.model.Product;
import java.math.BigDecimal;

public class ProductBuilder {
    private final Product product;

    public ProductBuilder() {
        product = new Product();
    }

    public static ProductBuilder aProduct() {
        return new ProductBuilder();
    }

    public ProductBuilder withName(String name) {
        product.setName(name);
        return this;
    }

    public ProductBuilder withBrand(String brand) {
        product.setBrand(brand);
        return this;
    }

    public ProductBuilder withPrice(BigDecimal price) {
        product.setPrice(price);
        return this;
    }

    public ProductBuilder withStock(Integer stock) {
        product.setStock(stock);
        return this;
    }

    public ProductBuilder withCategory(Category category) {
        product.setCategory(category);
        return this;
    }

    public ProductBuilder withDescription(String description) {
        product.setDescription(description);
        return this;
    }

    public Product build() {
        return product;
    }
}