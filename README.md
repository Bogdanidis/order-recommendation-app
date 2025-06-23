# Order App

A comprehensive Spring Boot e-commerce application featuring intelligent product recommendations, multi-layer security, and modern containerization support.

## Features

### Core Functionality
- **Product Management:** Full CRUD operations with category assignment, stock tracking, and image upload support
- **Order Processing:** Complete order lifecycle management with real-time status tracking
- **User Management:** Role-based access control (Admin/Customer) with comprehensive user profiles
- **Shopping Cart:** Persistent cart functionality with real-time updates

### Recommendation System
- **Multi-Algorithm Approach:** Dynamically selects optimal recommendation strategy based on user behavior:
  - **User-Based Collaborative Filtering:** For users with 5+ orders and ratings
  - **Item-Based Collaborative Filtering:** For users with 2-5 orders  
  - **Content-Based Filtering:** For new users with minimal interaction history
  - **Matrix Factorization:** Advanced algorithm for users with extensive rating data
- **Intelligent Scoring:** Combines purchase history, ratings, and product popularity
- **Paginated Results:** Efficiently handles large recommendation sets

### Security & Authentication
- **JWT Authentication:** Stateless token-based authentication for REST APIs
- **OAuth2 Integration:** Google OAuth2 login support (GitHub ready)
- **Multi-Layer Security:** Separate configurations for web and API endpoints
- **Role-Based Authorization:** Fine-grained access control with method-level security
- **Password Encryption:** BCrypt hashing with secure session management

### Performance & Scalability
- **Caffeine Caching:** Multi-level caching for products, categories, recommendations, and user data
- **Database Optimization:** Connection pooling, batch operations, and query optimization
- **Pagination:** Efficient data handling across all major entities
- **Soft Deletes:** Data integrity preservation with logical deletion

### REST API
- **Comprehensive API:** Full REST endpoints for all major functionalities
- **OpenAPI/Swagger:** Interactive API documentation at `/swagger-ui.html`
- **Standardized Responses:** Consistent response format across all endpoints
- **API Versioning:** Organized under `/order-api/v1/` prefix

### Data Management
- **Flyway Migrations:** Version-controlled database schema management
- **Test Data Generation:** JavaFaker integration for realistic sample data
- **Flexible Configuration:** Environment-specific settings with comprehensive property management
- **MySQL Integration:** Optimized for MySQL 8.0 with proper indexing

### Containerization
- **Docker Support:** Multi-stage Dockerfile for optimized production builds
- **Docker Compose:** Complete development environment with MySQL integration
- **Production Ready:** Separate build and runtime stages for minimal image size

### User Interface
- **Responsive Design:** Bootstrap 5 integration for mobile-friendly interface
- **Thymeleaf Templates:** Server-side rendering with dynamic content
- **Real-time Updates:** Live cart updates and recommendation refreshing
- **Admin Dashboard:** Comprehensive management interface for all entities

## Architecture

### Technology Stack
- **Backend:** Spring Boot 3.3.3, Spring Security, Spring Data JPA
- **Database:** MySQL 8.0 with Flyway migrations
- **Caching:** Caffeine with Spring Cache abstraction
- **Security:** JWT + OAuth2 (Google/GitHub)
- **Documentation:** OpenAPI 3 + Swagger UI
- **Frontend:** Thymeleaf + Bootstrap 5
- **Testing:** Spring Boot Test + Security Test
- **Containerization:** Docker + Docker Compose

### Key Dependencies
- **Security:** JWT (jjwt), OAuth2, Spring Security
- **Data:** MySQL Connector, Flyway, HikariCP
- **Utilities:** Lombok, ModelMapper, JavaFaker, Apache Commons Math
- **Monitoring:** Spring Actuator with cache metrics

<!-- ## API Endpoints

### Authentication
- `POST /order-api/v1/auth/login` - User login
- `POST /order-api/v1/auth/register` - User registration
- `GET /order-api/v1/auth/validate-token` - Token validation

### Products
- `GET /order-api/v1/products` - List products (paginated)
- `POST /order-api/v1/products` - Create product (Admin)
- `PUT /order-api/v1/products/{id}` - Update product (Admin)
- `DELETE /order-api/v1/products/{id}` - Delete product (Admin)

### Orders
- `GET /order-api/v1/orders` - List user orders
- `POST /order-api/v1/orders` - Create order
- `GET /order-api/v1/orders/{id}` - Get order details

### Recommendations
- Integrated into home page with intelligent algorithm selection
- Cached results for optimal performance -->

## Database Schema

The application uses Flyway for database versioning with the following key entities:
- **Users & Roles:** Role-based access control
- **Products & Categories:** Hierarchical product organization  
- **Orders & Order Items:** Complete order management
- **Shopping Cart:** Persistent cart functionality
- **Product Ratings:** User rating system for recommendations
- **Images:** Product image management with blob storage

### ER Diagram

Below is the Entity-Relationship diagram for the application:

![ER](ER.png)


## Quick Start

### Using Docker Compose (Recommended)

```bash
# Clone the repository
git clone <repository-url>
cd order-app

# Start the application with Docker Compose
docker-compose up -d

# Access the application
# Web Interface: http://localhost:8080
# API Documentation: http://localhost:8080/swagger-ui.html
```

### Manual Setup

**Prerequisites:**
- Java 17+
- Maven 3.6+
- MySQL 8.0+

**Steps:**
1. **Clone and navigate:**
   ```bash
   git clone <repository-url>
   cd order-app
   ```

2. **Database setup:**
   ```bash
   # Create database and user
   mysql -u root -p < src/main/resources/db/create_user.sql
   mysql -u root -p < src/main/resources/db/init/init-database.sql
   ```

3. **Configure application:**
   ```properties
   # Update src/main/resources/application.properties
   spring.datasource.url=jdbc:mysql://localhost:3306/order_app
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Build and run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

## Configuration

### Docker Environment Variables
```yaml
environment:
  - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/order_app
  - SPRING_DATASOURCE_USERNAME=nikos
  - SPRING_DATASOURCE_PASSWORD=nikos
```

### Key Application Properties
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/order_app
spring.jpa.hibernate.ddl-auto=validate

# Security
auth.token.expirationInMils=3600000
spring.security.oauth2.client.registration.google.client-id=your-client-id

# API
api.prefix=/order-api/v1
springdoc.swagger-ui.path=/swagger-ui.html

# Caching
cache.ttl.products=3600
cache.ttl.categories=7200
```


## Default Credentials

**Admin User:**
- Email: `admin@example.com`
- Password: `admin123`

**System Admin:**
- Email: `system@example.com`  
- Password: `system123`

## Development

### Building from Source
```bash
# Build application
mvn clean compile

# Run tests
mvn test

# Package application
mvn package

# Skip tests (faster build)
mvn package -DskipTests
```

### Docker Development
```bash
# Build custom image
docker build -t order-app .

# Run with custom configuration
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host:3306/order_app \
  order-app
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit changes (`git commit -am 'Add new feature'`)
4. Push to branch (`git push origin feature/new-feature`)
5. Create Pull Request

<!-- ## License

This project is licensed under the MIT License - see the LICENSE file for details. -->