package com.example.order_app.service.product;

import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.Product;
import com.example.order_app.repository.ProductRepository;
import com.example.order_app.request.AddProductRequest;
import com.example.order_app.service.category.ICategoryService;
import com.example.order_app.util.TestDataUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ICategoryService categoryService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private AddProductRequest addProductRequest;

    @BeforeEach
    void setUp() {
        testProduct = TestDataUtil.createTestProduct();
        addProductRequest = new AddProductRequest();
        // Setup addProductRequest with test data
        addProductRequest.setName(testProduct.getName());
        addProductRequest.setBrand(testProduct.getBrand());
        addProductRequest.setPrice(testProduct.getPrice());
        addProductRequest.setStock(testProduct.getStock());
        addProductRequest.setCategory(testProduct.getCategory());
    }

    @Test
    void shouldAddProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(categoryService.getCategoryById(any())).thenReturn(testProduct.getCategory());

        Product result = productService.addProduct(addProductRequest);

        assertNotNull(result);
        assertEquals(testProduct.getName(), result.getName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldGetProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Product result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(testProduct.getName(), result.getName());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void shouldGetAllProducts() {
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(testProduct));
        when(productRepository.findAll(any(PageRequest.class))).thenReturn(productPage);

        Page<Product> result = productService.getAllProducts(PageRequest.of(0, 10));

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(1, result.getContent().size());
    }
}
