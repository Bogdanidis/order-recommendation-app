package com.example.order_app.service.product;

import com.example.order_app.dto.ProductDto;
import com.example.order_app.model.Product;
import com.example.order_app.request.AddProductRequest;
import com.example.order_app.request.UpdateProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductService {
    Product addProduct(AddProductRequest product);
    Product getProductById(Long id);
    void deleteProductById(Long id);
    Product updateProduct(UpdateProductRequest product, Long productId);
    Page<Product> getAllProducts(Pageable pageable);
    List<Product> getProductsByCategory(String category);
    List<Product> getProductsByBrand(String brand);
    List<Product> getProductsByCategoryAndBrand(String category, String brand);
    List<Product> getProductsByName(String name);
    List<Product> getProductsByBrandAndName(String category, String name);
    Long countProductsByBrandAndName(String brand, String name);

    List<ProductDto> getConvertedProducts(List<Product> products);

    ProductDto convertToDto(Product product);

    List<ProductDto> searchProducts(String brandName, String productName, String category);
}
