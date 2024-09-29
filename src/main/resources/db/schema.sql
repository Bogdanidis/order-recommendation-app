DROP SCHEMA IF EXISTS order_app;

CREATE SCHEMA order_app;

USE
order_app;


CREATE TABLE users
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    username   VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    email      VARCHAR(255)        NOT NULL,
    role    VARCHAR(255)        NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


CREATE TABLE customers
(
    id         BIGINT PRIMARY KEY,
    last_name  VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    phone      VARCHAR(255) NOT NULL,
    city       VARCHAR(255) NOT NULL,
    country    VARCHAR(255) NOT NULL,
    FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE admins
(
    id BIGINT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE orders
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT       NOT NULL,
    date        date         NOT NULL,
    status      VARCHAR(255) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE TABLE categories
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL
);

CREATE TABLE products
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(255)  NOT NULL,
    description VARCHAR(255)  NOT NULL,
    stock       INTEGER       NOT NULL,
    price       DECIMAL(8, 2) NOT NULL,
    brand VARCHAR(255)  NOT NULL,
    category_id BIGINT  NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE order_products
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id   BIGINT  NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders (id),
    product_id BIGINT  NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products (id),
    quantity   INTEGER NOT NULL
);


CREATE TABLE images
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    file_name VARCHAR(255)       NOT NULL,
    file_type        VARCHAR(255)        NOT NULL,
    image      mediumblob NOT NULL,
    download_url VARCHAR(255) NOT NULL,
    product_id BIGINT  NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products (id)
);

