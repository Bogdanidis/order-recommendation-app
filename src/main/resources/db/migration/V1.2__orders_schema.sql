/*
 * V1.2 - Orders and Cart schema
 * Creates order and cart related tables with proper constraints
 */

SET autocommit = 0;
START TRANSACTION;

CREATE TABLE orders
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id      BIGINT                                                          NOT NULL,
    order_date   DATE                                                            NOT NULL,
    total_amount DECIMAL(38, 2)                                                  NOT NULL,
    order_status ENUM ('PENDING','PROCESSING','SHIPPED','DELIVERED','CANCELLED') NOT NULL,
    deleted      BOOLEAN                                                                  DEFAULT FALSE,
    deleted_at   TIMESTAMP                                                       NULL,
    created_at   TIMESTAMP                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP                                                       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_orders_total_amount CHECK (total_amount >= 0)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE order_items
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id   BIGINT         NOT NULL,
    product_id BIGINT         NOT NULL,
    quantity   INTEGER        NOT NULL,
    price      DECIMAL(38, 2) NOT NULL,
    created_at TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id)
        REFERENCES orders (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id)
        REFERENCES products (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_order_items_quantity CHECK (quantity > 0),
    CONSTRAINT chk_order_items_price CHECK (price >= 0)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE carts
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id      BIGINT         NOT NULL,
    total_amount DECIMAL(38, 2) NOT NULL DEFAULT 0,
    created_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_carts_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT uk_carts_user UNIQUE (user_id),
    CONSTRAINT chk_carts_total_amount CHECK (total_amount >= 0)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE cart_items
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    cart_id     BIGINT         NOT NULL,
    product_id  BIGINT         NOT NULL,
    quantity    INTEGER        NOT NULL,
    unit_price  DECIMAL(38, 2) NOT NULL,
    total_price DECIMAL(38, 2) NOT NULL,
    created_at  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id)
        REFERENCES carts (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id)
        REFERENCES products (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_cart_items_quantity CHECK (quantity > 0),
    CONSTRAINT chk_cart_items_prices CHECK (unit_price >= 0 AND total_price >= 0)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Create indexes
CREATE INDEX idx_orders_user ON orders (user_id);
CREATE INDEX idx_orders_status_date ON orders (order_status, order_date);
CREATE INDEX idx_orders_deleted ON orders (deleted);
CREATE INDEX idx_cart_items_cart ON cart_items (cart_id);
CREATE INDEX idx_cart_items_product ON cart_items (product_id);
CREATE INDEX idx_order_items_order ON order_items (order_id);
CREATE INDEX idx_order_items_product ON order_items (product_id);

COMMIT;