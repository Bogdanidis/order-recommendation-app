DROP SCHEMA IF EXISTS order_app;

CREATE SCHEMA order_app;

USE order_app;

CREATE TABLE customers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    last_name VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    -- username VARCHAR(255) NOT NULL,
    -- password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL
);

CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_customer
        FOREIGN KEY(user_id)
        REFERENCES customers(id)
);

CREATE TABLE products (
      id BIGINT PRIMARY KEY AUTO_INCREMENT,
      name VARCHAR(255) NOT NULL,
      description VARCHAR(255) NOT NULL,
      price DECIMAL NOT NULL
);

CREATE TABLE order_items (
       id BIGINT PRIMARY KEY AUTO_INCREMENT,
       order_id BIGINT NOT NULL,
       CONSTRAINT fk_order
            FOREIGN KEY(order_id)
            REFERENCES orders(id),
       product_id BIGINT NOT NULL,
       CONSTRAINT fk_product
           FOREIGN KEY(product_id)
           REFERENCES products(id),
        quantity INT NOT NULL

);


/*
CREATE TABLE categories (
      id BIGINT PRIMARY KEY AUTO_INCREMENT,
      name VARCHAR(255) NOT NULL,
      description VARCHAR(255) NOT NULL
);

CREATE TABLE product_category (
    category_id BIGINT NOT NULL,
       CONSTRAINT fk_category
            FOREIGN KEY(category_id)
            REFERENCES categories(id),
       product_id BIGINT NOT NULL,
       CONSTRAINT fk_category_product
           FOREIGN KEY(product_id)
           REFERENCES products(id),
    PRIMARY KEY(category_id,product_id)

);
 */
