# Spring Boot E-commerce Application

A full-featured e-commerce application built with Spring Boot supporting user authentication, product management, shopping cart functionality, order processing, and personalized recommendations.

## Features

- User authentication and role-based authorization (Admin/User)
- Product catalog with categories
- Shopping cart management
- Order processing
- Rating and review system
- Personalized product recommendations
- Admin dashboard
- Image upload and management
- RESTful API endpoints
- Responsive UI with Bootstrap 5

## Technologies

- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- Thymeleaf
- Bootstrap 5
- MySQL
- Project Lombok
- ModelMapper
- JWT Authentication (for API)

## Requirements

- JDK 17+
- Maven 3.x
- MySQL 8.x

## Installation

1. Clone the repository:
```bash
git clone https://github.com/your-username/order-app.git
```

2. Create MySQL database:
```sql
CREATE DATABASE order_app;
```

3. Configure application.properties with your database credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/order_app
spring.datasource.username=your_username
spring.datasource.password=your_password
```

4. Run the application:
```bash
mvn spring:boot run
```

## Default Users

The application comes with two default users:

```
Admin:
Email: admin@example.com
Password: admin

User:
Email: user@example.com 
Password: user
```

## Project Structure

```
src/main/java/
└── com.example.order_app
    ├── config/         # Configuration classes
    ├── controller/     # MVC & REST controllers
    ├── dto/           # Data Transfer Objects
    ├── model/         # Entity classes
    ├── repository/    # JPA repositories
    ├── security/      # Security configuration
    └── service/       # Business logic
```

## API Documentation

The application provides both web interface and REST API endpoints. Main API endpoints:

- `POST /order-api/v1/auth/login` - Authentication
- `GET /order-api/v1/products` - Get all products
- `GET /order-api/v1/orders` - Get user orders
- `POST /order-api/v1/cart/items/add` - Add item to cart

Full API documentation available via Swagger UI at `/swagger-ui.html`

## Features Overview

### Product Management
- CRUD operations for products
- Category organization
- Image management
- Product search and filtering

### Shopping Cart
- Add/remove items
- Update quantities
- Calculate totals
- Persist cart between sessions

### Order Processing
- Convert cart to order
- Order history
- Order status tracking
- Cancel orders

### Recommendation System
Implements multiple recommendation strategies:
- Content-based filtering
- Collaborative filtering
- Matrix factorization
- Item-based recommendations

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Submit a pull request

## License

This project is licensed under the MIT License.