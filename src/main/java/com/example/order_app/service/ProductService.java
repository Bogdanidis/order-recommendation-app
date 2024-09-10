package com.example.order_app.service;


import com.example.order_app.model.Product;
import com.example.order_app.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void orderProduct(Long productId, Integer quantity) {
        // Implement the logic to order the product, update stock, etc.
        // For simplicity, you can decrement stock and save the order
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getStock() >= quantity) {
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);
            // Add order to order repository (not shown here)
        } else {
            // Handle case where stock is insufficient
            throw new RuntimeException("Not enough stock available");
        }
    }
}
