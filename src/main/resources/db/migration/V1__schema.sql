USE order_app;

# CREATE TABLE users
# (
#     id         BIGINT PRIMARY KEY AUTO_INCREMENT,
#     first_name VARCHAR(255) NOT NULL,
#     last_name  VARCHAR(255) NOT NULL,
#     email      VARCHAR(255) NOT NULL,
#     password   VARCHAR(255) NOT NULL
#     -- phone      VARCHAR(255),
#     -- city       VARCHAR(255,
#     -- country    VARCHAR(255)
# );
#
# CREATE TABLE orders
# (
#     id           BIGINT PRIMARY KEY AUTO_INCREMENT,
#     order_date   DATE                                                            NOT NULL,
#     total_amount DECIMAL(38, 2)                                                  NOT NULL,
#     order_status ENUM ('PENDING','PROCESSING','SHIPPED','DELIVERED','CANCELLED') NOT NULL,
#     user_id      BIGINT                                                          NOT NULL,
#     FOREIGN KEY (user_id) REFERENCES users (id)
# );
#
# CREATE TABLE categories
# (
#     id          BIGINT PRIMARY KEY AUTO_INCREMENT,
#     name        VARCHAR(255) NOT NULL,
#     description VARCHAR(255) NOT NULL
# );
#
# CREATE TABLE products
# (
#     id             BIGINT PRIMARY KEY AUTO_INCREMENT,
#     name           VARCHAR(255)   NOT NULL,
#     description    VARCHAR(255)   NOT NULL,
#     stock          INTEGER        NOT NULL,
#     price          DECIMAL(38, 2) NOT NULL,
#     brand          VARCHAR(255)   NOT NULL,
#     average_rating DECIMAL(3, 2) DEFAULT 0.00,
#     rating_count   BIGINT        DEFAULT 0,
#     category_id    BIGINT         NOT NULL,
#     FOREIGN KEY (category_id) REFERENCES categories (id)
# );
#
# CREATE TABLE product_ratings
# (
#     id         BIGINT PRIMARY KEY AUTO_INCREMENT,
#     rating     INTEGER   NOT NULL CHECK (rating >= 1 AND rating <= 5),
#     comment    TEXT,
#     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
#     product_id BIGINT    NOT NULL,
#     user_id    BIGINT    NOT NULL,
#     FOREIGN KEY (product_id) REFERENCES products (id),
#     FOREIGN KEY (user_id) REFERENCES users (id),
#     UNIQUE KEY unique_user_product_rating (user_id, product_id)
# );
#
# CREATE TABLE order_items
# (
#     id         BIGINT PRIMARY KEY AUTO_INCREMENT,
#     quantity   INTEGER        NOT NULL,
#     price      DECIMAL(38, 2) NOT NULL,
#     order_id   BIGINT         NOT NULL,
#     FOREIGN KEY (order_id) REFERENCES orders (id),
#     product_id BIGINT         NOT NULL,
#     FOREIGN KEY (product_id) REFERENCES products (id)
# );
#
# CREATE TABLE carts
# (
#     id           BIGINT PRIMARY KEY AUTO_INCREMENT,
#     total_amount DECIMAL(38, 2) NOT NULL,
#     user_id      BIGINT         NOT NULL,
#     FOREIGN KEY (user_id) REFERENCES users (id)
#
# );
#
# CREATE TABLE cart_items
# (
#     id          BIGINT PRIMARY KEY AUTO_INCREMENT,
#     total_price DECIMAL(38, 2) NOT NULL,
#     unit_price  DECIMAL(38, 2) NOT NULL,
#     quantity    INTEGER        NOT NULL,
#     cart_id     BIGINT         NOT NULL,
#     FOREIGN KEY (cart_id) REFERENCES carts (id),
#     product_id  BIGINT         NOT NULL,
#     FOREIGN KEY (product_id) REFERENCES products (id)
#
# );
#
# CREATE TABLE roles
# (
#     id   BIGINT PRIMARY KEY AUTO_INCREMENT,
#     name VARCHAR(255) NOT NULL
# );
#
# CREATE TABLE user_roles
# (
#     id      BIGINT PRIMARY KEY AUTO_INCREMENT,
#     user_id BIGINT NOT NULL,
#     FOREIGN KEY (user_id) REFERENCES users (id),
#     role_id BIGINT NOT NULL,
#     FOREIGN KEY (role_id) REFERENCES roles (id)
# );
#
# CREATE TABLE images
# (
#     id           BIGINT PRIMARY KEY AUTO_INCREMENT,
#     file_name    VARCHAR(255) NOT NULL,
#     file_type    VARCHAR(255) NOT NULL,
#     image        MEDIUMBLOB,
#     download_url VARCHAR(255) NOT NULL,
#     product_id   BIGINT       NOT NULL,
#     FOREIGN KEY (product_id) REFERENCES products (id)
# );


