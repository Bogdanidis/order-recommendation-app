package com.example.order_app.util;


import com.example.order_app.model.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class TestDataUtil {

    public static User createTestUser() {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");
        user.setPassword("password");
        Set<Role> roles = new HashSet<>();
        roles.add(createUserRole());
        user.setRoles(roles);
        return user;
    }

    public static User createTestAdmin() {
        User admin = new User();
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setEmail("admin@example.com");
        admin.setPassword("password");
        Set<Role> roles = new HashSet<>();
        roles.add(createAdminRole());
        admin.setRoles(roles);
        return admin;
    }

    public static Role createUserRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        return role;
    }

    public static Role createAdminRole() {
        Role role = new Role();
        role.setId(2L);
        role.setName("ROLE_ADMIN");
        return role;
    }

    public static Product createTestProduct() {
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setBrand("Test Brand");
        product.setPrice(new BigDecimal("99.99"));
        product.setStock(10);
        product.setCategory(createTestCategory());
        return product;
    }

    public static Category createTestCategory() {
        Category category = new Category();
        category.setName("Test Category");
        category.setDescription("Test Category Description");
        return category;
    }
}