package com.example.order_app.util.fixture;

import com.example.order_app.model.Product;
import com.example.order_app.util.builder.ProductBuilder;
import java.math.BigDecimal;

public class ProductFixture {
    public static Product aDefaultProduct() {
        return ProductBuilder.aProduct()
                .withName("Test Product")
                .withBrand("Test Brand")
                .withPrice(new BigDecimal("99.99"))
                .withStock(10)
                .withDescription("Test Description")
                .withCategory(CategoryFixture.aDefaultCategory())
                .build();
    }

    public static Product aCustomProduct(String name, BigDecimal price) {
        return ProductBuilder.aProduct()
                .withName(name)
                .withBrand("Custom Brand")
                .withPrice(price)
                .withStock(10)
                .withDescription("Custom Description")
                .withCategory(CategoryFixture.aDefaultCategory())
                .build();
    }
}
