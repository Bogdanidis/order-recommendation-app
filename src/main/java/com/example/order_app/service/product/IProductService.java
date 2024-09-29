package com.example.order_app.service.product;

import com.example.order_app.model.Product;
import com.example.order_app.request.AddProductRequest;
import com.example.order_app.request.UpdateProductRequest;

import java.util.List;

public interface IProductService {
    Product addProduct(AddProductRequest request);
    Product getProductById(Long id);
    void deleteProductById(Long id);
    Product updateProduct(UpdateProductRequest product, Long productId);

    List<Product> getAllProducts();
    List<Product> getProductsByCategory(String category);
    List<Product> getProductsByBrand(String brand);
    List<Product> getProductsByCategoryAndBrand(String category, String brand);
    List<Product> getProductsByName(String name);
    List<Product> getProductsByBrandAndName(String name, String brand);

    Long countProducts();
    Long countProductsByCategory(String category);
    Long countProductsByBrand(String brand);
    Long countProductsByCategoryAndBrand(String category, String brand);
    Long countProductsByName(String name);
    Long countProductsByBrandAndName(String name, String brand);


}
