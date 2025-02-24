package com.example.order_app.service.data;

import com.example.order_app.dto.ProductRatingDto;
import com.example.order_app.enums.OrderStatus;
import com.example.order_app.model.*;
import com.example.order_app.repository.OrderRepository;
import com.example.order_app.repository.ProductRatingRepository;
import com.example.order_app.repository.ProductRepository;
import com.example.order_app.request.AddProductRequest;
import com.example.order_app.request.AddUserRequest;
import com.example.order_app.service.cart.ICartItemService;
import com.example.order_app.service.cart.ICartService;
import com.example.order_app.service.category.ICategoryService;
import com.example.order_app.service.order.IOrderService;
import com.example.order_app.service.product.IProductService;
import com.example.order_app.service.rating.IProductRatingService;
import com.example.order_app.service.role.IRoleService;
import com.example.order_app.service.user.IUserService;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabasePopulationService {
    private final IUserService userService;
    private final IProductService productService;
    private final IOrderService orderService;
    private final ICartService cartService;
    private final ICartItemService cartItemService;
    private final ICategoryService categoryService;
    private final IRoleService roleService;
    private final IProductRatingService productRatingService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductRatingRepository ratingRepository;

    private final Faker faker = new Faker();

    @Transactional
    protected void populateDatabase() {
        if (shouldPopulateData()) {
            List<User> users = populateUsers();
            List<Product> products = populateProducts();
            populateOrders(users, products);
            populateRatings(users, products);
        }
    }

    private boolean shouldPopulateData() {
        return userService.findAll().size() <= 2;
    }

    private List<User> populateUsers() {
        List<User> users = new ArrayList<>();
        Set<Role> userRoles = roleService.findByName("ROLE_USER");

        for (int i = 0; i < 50; i++) {
            try {
                AddUserRequest request = new AddUserRequest();
                request.setFirstName(faker.name().firstName());
                request.setLastName(faker.name().lastName());
                request.setEmail(faker.internet().emailAddress());
                request.setPassword(request.getEmail());
                request.setRoles(userRoles);

                User user = userService.addUser(request);
                users.add(user);
                log.debug("Created user: {}", user.getEmail());
            } catch (Exception e) {
                log.warn("Failed to create user: {}", e.getMessage());
            }
        }

        return users;
    }

    private List<Product> populateProducts() {
        List<Product> products = new ArrayList<>();
        List<Category> categories = categoryService.getAllCategories();

        for (int i = 0; i < 200; i++) {
            try {
                Category category = categories.get(faker.random().nextInt(categories.size()));
                AddProductRequest request = createProductRequest(category);
                Product product = productService.addProduct(request);
                products.add(product);
                log.debug("Created product: {}", product.getName());
            } catch (Exception e) {
                log.warn("Failed to create product: {}", e.getMessage());
            }
        }

        return products;
    }

    private AddProductRequest createProductRequest(Category category) {
        AddProductRequest request = new AddProductRequest();
        request.setName(generateProductName(category));
        request.setDescription(generateProductDescription());
        request.setBrand(generateBrand());
        request.setStock(faker.random().nextInt(1, 1000));
        request.setPrice(generatePrice(category));
        request.setCategory(category);
        return request;
    }

    private void populateOrders(List<User> users, List<Product> products) {
        for (User user : users) {
            try {
                // Initialize cart for user
                Cart cart = cartService.initializeNewCart(user);

                int numOrders = faker.random().nextInt(11); // 0-10 orders
                for (int i = 0; i < numOrders; i++) {
                    createOrderForUser(user, products, cart);
                }
            } catch (Exception e) {
                log.warn("Failed to create orders for user {}: {}", user.getEmail(), e.getMessage());
            }
        }
    }

    private void createOrderForUser(User user, List<Product> products, Cart cart) {
        try {
            // Clear any existing items in cart
            cartService.clearCart(cart.getId());

            // Add random items to cart
            int numItems = faker.random().nextInt(1, 6); // 1-5 items
            for (int i = 0; i < numItems; i++) {
                Product randomProduct = products.get(faker.random().nextInt(products.size()));
                cartItemService.addItemToCart(cart.getId(), randomProduct.getId(),
                        faker.random().nextInt(1, 4)); // 1-3 quantity
            }

            // Place order using OrderService
            Order order = orderService.placeOrder(user.getId());

            // Determine status and date
            int randomStatus = faker.random().nextInt(100);
            if (randomStatus < 70) { // 70% DELIVERED
                order.setOrderStatus(OrderStatus.DELIVERED);
                order.setOrderDate(LocalDate.now().minusDays(faker.random().nextInt(30, 90)));
            } else if (randomStatus < 80) { // 10% CANCELLED
                // Directly set the cancelled status instead of using service method
                order.setOrderStatus(OrderStatus.CANCELLED);
                order.setOrderDate(LocalDate.now().minusDays(faker.random().nextInt(1, 30)));

                // Return items to inventory for cancelled order
                order.getOrderItems().forEach(orderItem -> {
                    Product product = orderItem.getProduct();
                    product.setStock(product.getStock() + orderItem.getQuantity());
                    productRepository.save(product);
                });
            } else if (randomStatus < 90) { // 10% SHIPPED
                order.setOrderStatus(OrderStatus.SHIPPED);
                order.setOrderDate(LocalDate.now().minusDays(faker.random().nextInt(1, 7)));
            } else { // 10% PROCESSING
                order.setOrderStatus(OrderStatus.PROCESSING);
                order.setOrderDate(LocalDate.now().minusDays(faker.random().nextInt(1, 3)));
            }

            // Save the updated order
            orderRepository.save(order);

            log.debug("Created order {} for user {} with status {}",
                    order.getId(), user.getEmail(), order.getOrderStatus());
        } catch (Exception e) {
            log.warn("Failed to create order: {}", e.getMessage());
        }
    }

    private void populateRatings(List<User> users, List<Product> products) {
        log.info("Starting ratings population...");
        for (Product product : products) {
            log.debug("For product id {}", product.getId());
            try {
                // Get all users who have ordered this product
                List<User> eligibleUsers = orderRepository.findByOrderStatusNot(OrderStatus.CANCELLED)
                        .stream()
                        .filter(order -> order.getOrderItems().stream()
                                .anyMatch(item -> item.getProduct().getId().equals(product.getId())))
                        .map(Order::getUser)
                        .distinct()
                        .collect(Collectors.toList());
                log.debug("Found {} total eligible users", eligibleUsers.size());

                if (!eligibleUsers.isEmpty()) {
                    // Randomly select 1-3 users to rate the product
                    int numRatings = Math.min(faker.random().nextInt(1, 4), eligibleUsers.size());
                    Collections.shuffle(eligibleUsers);
                    List<User> selectedUsers = eligibleUsers.subList(0, numRatings);

                    for (User user : selectedUsers) {
                        try {
                            if (!productRatingService.hasUserRatedProduct(user.getId(), product.getId())) {
                                log.debug("Creating rating for product {} by user {}", product.getId(), user.getId());
                                ProductRatingDto ratingDto = new ProductRatingDto();
                                ratingDto.setRating(faker.random().nextInt(1, 5));
                                log.debug("Generated rating value: {} ", ratingDto.getRating());
                                ratingDto.setComment(generateRatingComment(ratingDto.getRating()));

                                // Create and save the rating directly
                                ProductRating rating = new ProductRating();
                                rating.setRating(ratingDto.getRating());
                                rating.setComment(ratingDto.getComment());
                                rating.setProduct(product);
                                rating.setUser(user);
                                rating.setCreatedAt(LocalDateTime.now());

                                ratingRepository.save(rating);

                                // Update product rating cache
                                double avgRating = ratingRepository.calculateAverageRating(product.getId());
                                long ratingCount = ratingRepository.countByProductId(product.getId());
                                product.setAverageRating(avgRating);
                                product.setRatingCount(ratingCount);
                                productRepository.save(product);

                                log.debug("Created rating for product {} by user {}", product.getId(), user.getId());
                            }
                            else {
                                log.debug("User {} has already rated product {}", user.getId(), product.getId());
                            }
                        } catch (Exception e) {
                            log.warn("Failed to create individual rating for product {} by user {}: {}",
                                    product.getId(), user.getId(), e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to process ratings for product {}: {}", product.getId(), e.getMessage());
            }
        }
    }

    private String generateProductName(Category category) {
        String name = "Electronics".equals(category.getName())
                ? faker.commerce().productName() + " " + faker.options().option("Pro", "Ultra", "Max", "Elite")
                : faker.commerce().productName();
        return name.length() > 255 ? name.substring(0, 252) + "..." : name;
    }

    private String generateBrand() {
        String brand = faker.company().name().split(" ")[0].replace(",","");
        return brand.length() > 255 ? brand.substring(0, 252) + "..." : brand;
    }

    private String generateProductDescription() {
        String description = faker.lorem().paragraph() + "\n\nFeatures:\n- " +
                faker.lorem().sentence() + "\n- " +
                faker.lorem().sentence() + "\n- " +
                faker.lorem().sentence();
        return description.length() > 255 ? description.substring(0, 252) + "..." : description;
    }

    private BigDecimal generatePrice(Category category) {
        double basePrice = faker.number().randomDouble(2, 10, 1000);

        if ("Electronics".equals(category.getName())) {
            basePrice *= 2.5;
        } else if ("Books".equals(category.getName())) {
            basePrice *= 0.4;
        }

        return BigDecimal.valueOf(Math.max(0.01, basePrice));
    }

    private String generateRatingComment(int rating) {
        String sentiment = switch (rating) {
            case 5 -> faker.options().option(
                    "Excellent", "Outstanding", "Amazing", "Perfect", "Fantastic");
            case 4 -> faker.options().option(
                    "Very good", "Great", "Solid", "Really nice", "Good quality");
            case 3 -> faker.options().option(
                    "Decent", "Average", "Okay", "Fair", "Satisfactory");
            default -> faker.options().option(
                    "Disappointing", "Could be better", "Not great", "Below average");
        };

        return sentiment + " product. " + faker.lorem().paragraph();
    }
}
