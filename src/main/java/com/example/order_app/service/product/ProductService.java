package com.example.order_app.service.product;

import com.example.order_app.dto.ImageDto;
import com.example.order_app.dto.ProductDto;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.*;
import com.example.order_app.repository.*;
import com.example.order_app.request.AddProductRequest;
import com.example.order_app.request.UpdateProductRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository; // To find customer details if needed
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;

    private final ModelMapper modelMapper;

    @Override
    public Product addProduct(AddProductRequest request) {
        /*
        For the product's Category check if it exists in DB
        If it does, set it as its category, otherwise save it as a new category
         */
        Category category= Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
                .orElseGet(()->{
                    Category newCategory = new Category(request.getCategory().getName(),request.getCategory().getDescription());
                    return categoryRepository.save(newCategory);
                });
        request.setCategory(category);
        return productRepository.save(createProduct(request,category));
    }

    private Product createProduct(AddProductRequest request, Category category) {
        return new Product(
                request.getName(),
                request.getDescription(),
                request.getStock(),
                request.getPrice(),
                request.getBrand(),
                category
        );
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Product not found!"));
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id)
                .ifPresentOrElse(productRepository::delete,
                        ()->{throw new ResourceNotFoundException("Product not found!");});
    }

    @Override
    public Product updateProduct(UpdateProductRequest request, Long productId) {
        return productRepository.findById(productId)
                .map(existingProduct->updateExistingProduct(existingProduct,request))
                .map(productRepository::save)
                .orElseThrow(()->new ResourceNotFoundException("Product not found!"));

    }

    private Product updateExistingProduct(Product existingProduct, UpdateProductRequest request) {
        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setStock(request.getStock());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setBrand(request.getBrand());
        Category category = categoryRepository.findByName(request.getCategory().getName());
        existingProduct.setCategory(category);
        return existingProduct;
    }
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrand(category,brand);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<Product> getProductsByBrandAndName(String name, String brand) {
        return productRepository.findByBrandAndName(brand,name);
    }

    @Override
    public Long countProducts() {
        return productRepository.count();
    }

    @Override
    public Long countProductsByCategory(String category) {
        return productRepository.countByCategoryName(category);
    }

    @Override
    public Long countProductsByBrand(String brand) {
        return productRepository.countByBrand(brand);
    }

    @Override
    public Long countProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.countByCategoryNameAndBrand(category,brand);
    }

    @Override
    public Long countProductsByName(String name) {
        return productRepository.countByName(name);
    }


    @Override
    public Long countProductsByBrandAndName(String name, String brand) {
        return productRepository.countByBrandAndName(brand,name);
    }

    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products) {
        return products.stream().map(this::convertToDto).toList();
    }

    @Override
    public ProductDto convertToDto(Product product) {
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        List<Image> images = imageRepository.findByProductId(product.getId());
        List<ImageDto> imageDtos = images.stream()
                .map(image -> modelMapper.map(image, ImageDto.class))
                .toList();
        productDto.setImages(imageDtos);
        return productDto;
    }
    public void orderProduct(Long productId, Integer quantity, Long customerId) {
        // Fetch product by ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if enough stock is available
        if (product.getStock() < quantity) {
            throw new RuntimeException("Not enough stock available");
        }

        // Update stock
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        // Create a new Order
        Order order = new Order();

        // Link order to a customer if needed
        order.setCustomer(customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found")));

        //Add date to order
        order.setDate(new java.sql.Date(System.currentTimeMillis()));

        //Add status to date
        order.setStatus("Pending");

        // Create an OrderProduct relationship
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);
        orderProduct.setQuantity(quantity);
        orderProduct.setOrder(order);

        // Add OrderProduct to Order
        order.getOrderProducts().add(orderProduct);


        // Save the order
        orderRepository.save(order);
    }


}
