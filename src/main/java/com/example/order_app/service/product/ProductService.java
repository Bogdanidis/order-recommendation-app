package com.example.order_app.service.product;

import com.example.order_app.dto.ImageDto;
import com.example.order_app.dto.ProductDto;
import com.example.order_app.exception.AlreadyExistsException;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.*;
import com.example.order_app.repository.*;
import com.example.order_app.request.AddProductRequest;
import com.example.order_app.request.UpdateProductRequest;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ImageRepository imageRepository;

    @Override
    public Product addProduct(AddProductRequest request) {
        if (productExists(request.getName(), request.getBrand())){
            throw new AlreadyExistsException(request.getBrand() +" "+request.getName()+ " already exists, you may update this product instead!");
        }
        // check if the category is found in the DB
        // If Yes, set it as the new product category
        // If No, the save it as a new category
        // The set as the new product category.
        Category category = Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
                .orElseGet(() -> {
                    Category newCategory = new Category(request.getCategory().getName(), request.getCategory().getDescription());

                    return categoryRepository.save(newCategory);
                });
        request.setCategory(category);
        return productRepository.save(createProduct(request, category));
    }

    private boolean productExists(String name , String brand) {
        return productRepository.existsByNameAndBrand(name, brand);
    }

    private Product createProduct(AddProductRequest request, Category category) {
        return new Product(
                request.getName(),
                request.getBrand(),
                request.getStock(),
                request.getPrice(),
                request.getDescription(),
                category
        );
    }


    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found!"));
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id)
                .ifPresentOrElse(productRepository::delete,
                        () -> {throw new ResourceNotFoundException("Product not found!");});
    }

    @Override
    public Product updateProduct(UpdateProductRequest request, Long productId) {
        return productRepository.findById(productId)
                .map(existingProduct -> updateExistingProduct(existingProduct,request))
                .map(productRepository :: save)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found!"));
    }

    private Product updateExistingProduct(Product existingProduct, UpdateProductRequest request) {
        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setStock(request.getStock());
        existingProduct.setDescription(request.getDescription());

        Category category = categoryRepository.findByName(request.getCategory().getName());
        existingProduct.setCategory(category);
        return  existingProduct;

    }


    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
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
        return productRepository.findByCategoryNameAndBrand(category, brand);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand, name);
    }
    @Override
    public Long countProducts() {
        return productRepository.count();
    }
    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand, name);
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

//    @Override
//    public List<ProductDto> searchProducts(String brandName, String productName, String category) {
//        List<ProductDto> products = new ArrayList<>();
//
//        if (!StringUtils.isBlank(brandName) && !StringUtils.isBlank(productName)) {
//            products = getConvertedProducts(getProductsByBrandAndName(brandName, productName));
//        } else if (!StringUtils.isBlank(category) && !StringUtils.isBlank(brandName)) {
//            products = getConvertedProducts(getProductsByCategoryAndBrand(category, brandName));
//        } else if (!StringUtils.isBlank(productName)) {
//            products = getConvertedProducts(getProductsByName(productName));
//        } else if (!StringUtils.isBlank(brandName)) {
//            products = getConvertedProducts(getProductsByBrand(brandName));
//        } else if (!StringUtils.isBlank(category)) {
//            products = getConvertedProducts(getProductsByCategory(category));
//        }
//        return products;
//    }

    @Override
    public Page<ProductDto> searchProducts(Pageable pageable, String brand, String name, String category) {
        if ((brand != null && !brand.isEmpty()) ||
                (name != null && !name.isEmpty()) ||
                (category != null && !category.isEmpty())) {
            return productRepository.findByBrandContainingIgnoreCaseAndNameContainingIgnoreCaseAndCategoryNameContainingIgnoreCase(
                    brand != null ? brand : "",
                    name != null ? name : "",
                    category != null ? category : "",
                    pageable
            ).map(this::convertToDto);
        } else {
            return productRepository.findAll(pageable).map(this::convertToDto);
        }
    }
}
