/*
 * V1.0 - Initial schema
 * Creates core tables with proper auditing, constraints and indexes
 */

SET autocommit = 0;
START TRANSACTION;

-- User management tables
CREATE TABLE users
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    deleted    BOOLEAN               DEFAULT FALSE,
    deleted_at TIMESTAMP    NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE categories
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(255)  NOT NULL,
    description VARCHAR(1000) NOT NULL,
    deleted     BOOLEAN                DEFAULT FALSE,
    deleted_at  TIMESTAMP     NULL,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_categories_name UNIQUE (name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE products
(
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    name           VARCHAR(255)   NOT NULL,
    description    TEXT           NOT NULL,
    stock          INTEGER        NOT NULL,
    price          DECIMAL(38, 2) NOT NULL,
    brand          VARCHAR(255)   NOT NULL,
    category_id    BIGINT         NOT NULL,
    average_rating DECIMAL(3, 2)           DEFAULT 0.00,
    rating_count   BIGINT                  DEFAULT 0,
    deleted        BOOLEAN                 DEFAULT FALSE,
    deleted_at     TIMESTAMP      NULL,
    created_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id)
        REFERENCES categories (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_products_price CHECK (price > 0),
    CONSTRAINT chk_products_stock CHECK (stock >= 0)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Create indexes
CREATE INDEX idx_products_brand_name ON products (brand, name);
CREATE INDEX idx_products_category ON products (category_id);
CREATE INDEX idx_products_rating ON products (average_rating, rating_count);
CREATE INDEX idx_products_deleted ON products (deleted);
CREATE INDEX idx_categories_deleted ON categories (deleted);
CREATE INDEX idx_users_deleted ON users (deleted);

COMMIT;