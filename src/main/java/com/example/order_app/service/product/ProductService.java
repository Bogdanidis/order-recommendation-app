package com.example.order_app.service.product;

import com.example.order_app.dto.ImageDto;
import com.example.order_app.dto.ProductDto;
import com.example.order_app.exception.AlreadyExistsException;
import com.example.order_app.exception.ResourceNotFoundException;
import com.example.order_app.model.*;
import com.example.order_app.repository.*;
import com.example.order_app.request.AddProductRequest;
import com.example.order_app.request.SearchRequest;
import com.example.order_app.request.UpdateProductRequest;
import com.example.order_app.specification.ProductSpecificationsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ImageRepository imageRepository;
    private final OrderRepository orderRepository;

    @CacheEvict(value = "products", allEntries = true)
    @Override
    @Transactional
    public Product addProduct(AddProductRequest request) {
        if (productExists(request.getName(), request.getBrand())) {
            throw new AlreadyExistsException(request.getBrand() + " " + request.getName() + " already exists, you may update this product instead!");
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

    private boolean productExists(String name, String brand) {
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

    @Cacheable(value = "products", key = "#id", unless = "#result.deleted")
    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found!"));
    }

    @CacheEvict(value = "products", allEntries = true)
    @Override
    @Transactional
    public void deleteProductById(Long id) {
        Product product = getProductById(id);

        // Images and ratings will be soft deleted due to cascade

        // Update category product count if needed
        Category category = product.getCategory();
        if (category != null) {
            // Update category statistics if needed
        }

        productRepository.softDelete(id, LocalDateTime.now());

    }

    @CacheEvict(value = "products", allEntries = true)
    @Override
    @Transactional
    public Product updateProduct(UpdateProductRequest request, Long productId) {
        return productRepository.findById(productId)
                .map(existingProduct -> updateExistingProduct(existingProduct, request))
                .map(productRepository::save)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found!"));
    }

    private Product updateExistingProduct(Product existingProduct, UpdateProductRequest request) {
        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setStock(request.getStock());
        existingProduct.setDescription(request.getDescription());

        Category category = categoryRepository.findByName(request.getCategory().getName());
        existingProduct.setCategory(category);
        return existingProduct;

    }

    @Cacheable(value = "products", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
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
    public boolean hasUserPurchasedProduct(Long userId, Long productId) {
        return orderRepository.existsByUserIdAndOrderItemsProductId(userId, productId);
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
//    public Page<ProductDto> searchProducts(Pageable pageable, String brand, String name, String category) {
//        if ((brand != null && !brand.isEmpty()) ||
//                (name != null && !name.isEmpty()) ||
//                (category != null && !category.isEmpty())) {
//            return productRepository.findByBrandContainingIgnoreCaseAndNameContainingIgnoreCaseAndCategoryNameContainingIgnoreCase(
//                    brand != null ? brand : "",
//                    name != null ? name : "",
//                    category != null ? category : "",
//                    pageable
//            ).map(this::convertToDto);
//        } else {
//            return productRepository.findAll(pageable).map(this::convertToDto);
//        }
//    }

    @Override
    public Page<ProductDto> searchProducts(SearchRequest request, Pageable pageable) {
        ProductSpecificationsBuilder builder = new ProductSpecificationsBuilder();

        // Ensure request is not null
        if (request == null) {
            request = new SearchRequest();
        }

        // Build specification based on search criteria - only add non-empty fields
        if (StringUtils.isNotEmpty(request.getName())) {
            builder.with("name", ":", request.getName());
        }

        if (StringUtils.isNotEmpty(request.getBrand())) {
            builder.with("brand", ":", request.getBrand());
        }

        if (StringUtils.isNotEmpty(request.getCategoryName())) {
            builder.with("category.name", ":", request.getCategoryName());
        }

        if (request.getMinPrice() != null) {
            builder.with("price", ">", request.getMinPrice());
        }

        if (request.getMaxPrice() != null) {
            builder.with("price", "<", request.getMaxPrice());
        }

        if (request.getMinStock() != null) {
            builder.with("stock", ">", request.getMinStock());
        }

        if (Boolean.TRUE.equals(request.getInStock())) {
            builder.with("stock", ">", 0);
        }

        // Add 'deleted = false' criteria
        builder.with("deleted", "=", false);

        // Create specification
        Specification<Product> spec = builder.build();

        // Create pageable with sorting
        Pageable sortedPageable = pageable;

        String sortBy = request.getSortBy();
        String sortDirection = request.getSortDirection();

        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ?
                    Sort.Direction.DESC : Sort.Direction.ASC;

            sortedPageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(direction, sortBy)
            );

            log.debug("Sorting by: {} {}", sortBy, sortDirection);
        }

        // Execute query with specification and sorting
        Page<Product> products = productRepository.findAll(spec, sortedPageable);

        return products.map(this::convertToDto);
    }
}
