package com.example.order_app.util.fixture;

import com.example.order_app.model.Category;

public class CategoryFixture {
    public static Category aDefaultCategory() {
        Category category = new Category();
        category.setName("Test Category");
        category.setDescription("Test Category Description");
        return category;
    }
}