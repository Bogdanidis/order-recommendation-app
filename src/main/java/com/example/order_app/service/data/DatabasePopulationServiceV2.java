package com.example.order_app.service.data;

import com.example.order_app.dto.ProductRatingDto;
import com.example.order_app.enums.OrderStatus;
import com.example.order_app.model.*;
import com.example.order_app.repository.*;
import com.example.order_app.service.rating.ProductRatingService;
import com.example.order_app.service.role.IRoleService;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabasePopulationServiceV2 {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ProductRatingRepository ratingRepository;
    private final CategoryRepository categoryRepository;
    private final IRoleService roleService;
    private final TransactionTemplate transactionTemplate;
    private final PasswordEncoder passwordEncoder;


    private final ProductRatingService productRatingService;


    private final Faker faker = new Faker();

    @Value("${app.db.population.batch-size:100}")
    private int batchSize;

    @Value("${app.db.population.retry-attempts:3}")
    private int retryAttempts;

    @Transactional
    public void populateDatabase() {
        try {
            log.info("Starting database population with batch size: {}", batchSize);

            List<User> users = populateUsersInBatches();
            List<Product> products = populateProductsInBatches();
            populateOrdersInBatches(users, products);
            populateRatingsInBatches(users, products);

            log.info("Database population completed successfully");
        } catch (Exception e) {
            log.error("Error during database population", e);
            throw new RuntimeException("Database population failed", e);
        }
    }

    private List<User> populateUsersInBatches() {
        List<User> allUsers = new ArrayList<>();
        int totalUsers = 50;
        int batches = (totalUsers + batchSize - 1) / batchSize;

        Set<Role> userRoles = roleService.findByName("ROLE_USER");

        for (int i = 0; i < batches; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, totalUsers);

            int finalI = i;
            List<User> batchUsers = transactionTemplate.execute(status -> {
                List<User> users = new ArrayList<>();
                for (int j = start; j < end; j++) {
                    try {
                        User user = createRandomUser(userRoles);
                        users.add(userRepository.save(user));
                        log.debug("Created user: {}", user.getEmail());
                    } catch (Exception e) {
                        log.warn("Failed to create user in batch {}", finalI, e);
                    }
                }
                return users;
            });

            if (batchUsers != null) {
                allUsers.addAll(batchUsers);
            }
        }

        log.info("Created {} users", allUsers.size());
        return allUsers;
    }

    private User createRandomUser(Set<Role> roles) {
        User user = new User();
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());
        String email = faker.internet().emailAddress();
        user.setEmail(email);
        // Properly encode the password
        user.setPassword(passwordEncoder.encode(email)); // Using email as password but properly encoded
        user.setRoles(roles);
        return user;
    }

    private List<Product> populateProductsInBatches() {
        List<Product> allProducts = new ArrayList<>();
        int totalProducts = 200;
        int batches = (totalProducts + batchSize - 1) / batchSize;

        List<Category> categories = categoryRepository.findAll();

        for (int i = 0; i < batches; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, totalProducts);

            int finalI = i;
            List<Product> batchProducts = transactionTemplate.execute(status -> {
                List<Product> products = new ArrayList<>();
                for (int j = start; j < end; j++) {
                    try {
                        Category category = categories.get(faker.random().nextInt(categories.size()));
                        Product product = createRandomProduct(category);
                        products.add(productRepository.save(product));
                        log.debug("Created product: {}", product.getName());
                    } catch (Exception e) {
                        log.warn("Failed to create product in batch {}", finalI, e);
                    }
                }
                return products;
            });

            if (batchProducts != null) {
                allProducts.addAll(batchProducts);
            }
        }

        log.info("Created {} products", allProducts.size());
        return allProducts;
    }

    private Product createRandomProduct(Category category) {
        String name = "Electronics".equals(category.getName()) ?
                faker.commerce().productName() + " " + faker.options().option("Pro", "Ultra", "Max", "Elite") :
                faker.commerce().productName();

        Product product = new Product();
        product.setName(name);
        product.setBrand(generateBrand());
        product.setPrice(generatePrice(category));
        product.setStock(faker.random().nextInt(100, 1000));
        product.setDescription(generateProductDescription());
        product.setCategory(category);
        return product;
    }

    private void populateOrdersInBatches(List<User> users, List<Product> products) {
        int ordersPerUser = 5; // Average number of orders per user

        for (User user : users) {
            transactionTemplate.execute(status -> {
                int numOrders = faker.random().nextInt(ordersPerUser + 1); // 0 to 5 orders
                for (int i = 0; i < numOrders; i++) {
                    try {
                        createRandomOrder(user, products);
                    } catch (Exception e) {
                        log.warn("Failed to create order for user: {}", user.getEmail(), e);
                    }
                }
                return null;
            });
        }
    }

    private void createRandomOrder(User user, List<Product> products) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(generateRandomOrderDate());
        order.setOrderStatus(generateRandomOrderStatus());

        int numItems = faker.random().nextInt(1, 6); // 1-5 items
        Set<OrderItem> orderItems = new HashSet<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (int i = 0; i < numItems; i++) {
            Product product = products.get(faker.random().nextInt(products.size()));
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(faker.random().nextInt(1, 4)); // 1-3 quantity
            item.setPrice(product.getPrice());

            totalAmount = totalAmount.add(product.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity())));

            orderItems.add(item);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        orderRepository.save(order);
    }

    private void populateRatingsInBatches(List<User> users, List<Product> products) {
        for (Product product : products) {
            transactionTemplate.execute(status -> {
                try {
                    List<User> eligibleUsers = findEligibleUsersForRating(product);
                    if (!eligibleUsers.isEmpty()) {
                        int numRatings = Math.min(faker.random().nextInt(1, 4), eligibleUsers.size());
                        Collections.shuffle(eligibleUsers);

                            for (int i = 0; i < numRatings; i++) {
                            try {
                                if (!ratingRepository.existsByUserIdAndProductId(
                                        eligibleUsers.get(i).getId(), product.getId())) {
                                    createRandomRating(eligibleUsers.get(i), product);
                                }
                            } catch (Exception e) {
                                log.warn("Failed to create individual rating for product {} by user {}: {}",
                                        product.getId(), eligibleUsers.get(i).getId(), e.getMessage());
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("Failed to create ratings for product: {}", product.getId(), e);
                }
                return null;
            });
        }
    }

    //good
    private List<User> findEligibleUsersForRating(Product product) {
        return orderRepository.findByOrderStatusNot(OrderStatus.CANCELLED)
                .stream()
                .filter(order -> order.getOrderItems().stream()
                        .anyMatch(item -> item.getProduct().getId().equals(product.getId())))
                .map(Order::getUser)
                .distinct()
                .collect(Collectors.toList());
    }

    private void createRandomRating(User user, Product product) {
        try {
            log.debug("Creating rating with user ID: {} and product ID: {}",
                    user.getId(), product.getId());
            ProductRating rating = new ProductRating();
            rating.setRating(faker.random().nextInt(1, 5));
            rating.setComment(generateRatingComment(rating.getRating()));
            rating.setProduct(product);
            rating.setUser(user);
            // Also add the rating to the product's list
            product.getRatings().add(rating);
            // Also add the rating to the user's rating list
            user.getRatings().add(rating);

            ProductRating savedRating = ratingRepository.save(rating);

            log.debug("Saved rating with ID: {}, user ID: {}, product ID: {}",
                    savedRating.getId(), savedRating.getUser().getId(),
                    savedRating.getProduct().getId());

            // Then update product rating cache
            updateProductRatingCache(product);
        } catch (Exception e) {
            log.error("Error creating rating: {}", e.getMessage(), e);
            throw e;
        }
    }
    //good
    private void updateProductRatingCache(Product product) {
        try {
            // Get fresh rating statistics
            Double avgRating = ratingRepository.getAverageRating(product.getId());
            Long ratingCount = ratingRepository.getRatingCount(product.getId());

            // Update the product entity
            if (avgRating != null && ratingCount != null) {
                product.setAverageRating(avgRating);
                product.setRatingCount(ratingCount);
                productRepository.save(product);
            }
        } catch (Exception e) {
            log.warn("Error updating product rating cache: {}", e.getMessage());
        }
    }

    // Helper methods
    private String generateBrand() {
        String brand = faker.company().name().split(" ")[0].replace(",", "");
        return brand.length() > 255 ? brand.substring(0, 252) + "..." : brand;
    }

    private String generateProductDescription() {
        return faker.lorem().paragraph() + "\n\nFeatures:\n- " +
                faker.lorem().sentence() + "\n- " +
                faker.lorem().sentence() + "\n- " +
                faker.lorem().sentence();
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

    private LocalDate generateRandomOrderDate() {
        return LocalDate.now().minusDays(faker.random().nextInt(1, 90));
    }

    private OrderStatus generateRandomOrderStatus() {
        int randomStatus = faker.random().nextInt(100);
        if (randomStatus < 70) return OrderStatus.DELIVERED;
        if (randomStatus < 80) return OrderStatus.CANCELLED;
        if (randomStatus < 90) return OrderStatus.SHIPPED;
        return OrderStatus.PROCESSING;
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