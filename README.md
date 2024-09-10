# Order Recommendation Application

This project is a simple e-commerce application that allows users to browse products, place orders, and manage accounts. The application supports user roles, including customers and admins, with different home pages and functionalities for each role.

## Project Tech Stack

- **Java 17**
- **Spring Boot 3.x**
    - Spring MVC
    - Spring Data JPA
    - Spring Security 6.x
- **MySQL**: Database for storing user, product, order, and category data.
- **Thymeleaf**: Template engine for rendering HTML views.
- **Bootstrap 5**: For responsive UI design.
- **Maven**: Build and dependency management tool.

## Requirements

- **JDK 17**: Ensure Java 17 is installed and configured on your machine.
- **MySQL**: Set up a MySQL server and create a database named `order_app`.
- **Maven**: Install Maven for building and running the project.

## ER Diagram
Below is the Entity-Relationship (ER) diagram for the application:

![ER](ER.png)

The diagram outlines the database schema, including tables for users, customers, admins, orders, products, categories, and their relationships.

## Key Features

## Key Features

- **User Authentication and Authorization**
  - **Secure Login:** Users can securely log in using their credentials, with passwords hashed for security.
  - **Role-Based Access Control:** Users are assigned roles (`ADMIN` or `CUSTOMER`) which determine their access level within the application.
    - **Admin Role:** Grants access to management functionalities such as viewing and managing products, orders, and user accounts.
    - **Customer Role:** Allows access to browsing products, placing orders, and viewing personal order history.
  - **Custom User Entities:** The application uses an abstract `User` entity extended by `Admin` and `Customer` entities, enabling role-specific functionality and data.

- **Dynamic Home Pages**
  - **Role-Specific Dashboards:** Upon login, users are redirected to a home page tailored to their role.
    - **Admin Dashboard:** Includes options for:
      - **Product Management:** Add, update, or delete products.
      - **Order Management:** View, update, or delete customer orders; change order statuses.
      - **User Management:** View and manage user accounts and roles.
    - **Customer Dashboard:** Includes options for:
      - **Browse Products:** View available products in a card format with product details.
      - **My Orders:** View a list of all orders placed by the customer, with details and statuses.

- **Product Management**
  - **Product Listings:** Admins can add new products, edit existing product details, and manage inventory levels.
  - **Category Assignment:** Products can be categorized, and users can filter products by category when browsing.
  <!-- 
  - **Stock Management:** Track product stock levels and update them as necessary.
  -->
- **Order Management**
  - **Order Placement:** Customers can place orders for products directly from the product browsing interface.
  - **Order Tracking:** Customers can view the status of their orders, with updates as they progress through different stages (e.g., Pending, Shipped, Delivered).
  - **Order Details:** View detailed information about each order, including product quantities, prices, and total costs.
<!--
- **User Registration**
  - **Registration Page:** A unified registration form allows new users to sign up as either an Admin or a Customer.
  - **Dynamic Form Fields:** Customer-specific fields such as First Name, Last Name, Phone, City, and Country appear dynamically based on the role selection.
  - **Form Validation:** Real-time form validation feedback is provided to guide users in filling out required fields correctly.

- **Responsive Design**
  - **Bootstrap 5 Integration:** The application uses Bootstrap 5 for responsive design, ensuring a consistent look and feel across all devices.
  - **User-Friendly Interface:** Clean and modern UI elements make navigation and use of the application intuitive for all users.

- **Database Schema**
  - **Optimized for Performance:** The schema is designed to minimize redundancy and improve query performance, with proper indexing and relationships.
  - **Referential Integrity:** Foreign key constraints ensure data integrity across related tables (e.g., Users, Orders, Products).

- **Security Features**
  - **CSRF Protection:** Cross-Site Request Forgery protection is enabled to safeguard against malicious actions.
  - **Password Encryption:** User passwords are encrypted using secure hashing algorithms to prevent unauthorized access.
  - **Session Management:** Secure session management practices are employed to maintain the integrity of user sessions.

- **Extensibility**
  - **Modular Design:** The application is built with extensibility in mind, allowing easy addition of new features or changes to existing functionality.
  - **API Ready:** The backend is designed to easily expose APIs for future integrations, such as a mobile app or external services.

- **Error Handling and Logging**
  - **Comprehensive Error Handling:** The application gracefully handles errors, providing meaningful feedback to users.
  - **Logging:** Application events and errors are logged for monitoring and debugging purposes.
-->

## Setup Instructions

### 1. Clone the Repository

Clone the repository to your local machine using the following command:

```bash
git clone https://github.com/yourusername/ecommerce-app.git
cd ecommerce-app
```

### 2. Configure the Application
Update the src/main/resources/application.properties file with your MySQL database credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/order_app
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

### 3. Build the Project
Build the project using Maven:
```bash
mvn clean install
```

### 4. Run the Application
Run the application using the following command:
```bash
mvn spring-boot:run
```
### 5. Access the Application
Open your browser and go to http://localhost:8080 to access the application.


