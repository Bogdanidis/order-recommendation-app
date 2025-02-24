/*
 * V1.3 - Ratings and Images schema
 * Creates rating and image related tables with proper constraints
 */

SET autocommit = 0;
START TRANSACTION;

CREATE TABLE product_ratings
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT    NOT NULL,
    user_id    BIGINT    NOT NULL,
    rating     INTEGER   NOT NULL,
    comment    TEXT,
    deleted    BOOLEAN            DEFAULT FALSE,
    deleted_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ratings_product FOREIGN KEY (product_id)
        REFERENCES products (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ratings_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT uk_product_user_rating UNIQUE (product_id, user_id),
    CONSTRAINT chk_rating_value CHECK (rating BETWEEN 1 AND 5)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE images
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id   BIGINT       NOT NULL,
    file_name    VARCHAR(255) NOT NULL,
    file_type    VARCHAR(255) NOT NULL,
    image        MEDIUMBLOB   NOT NULL,
    download_url VARCHAR(255) NOT NULL,
    deleted      BOOLEAN               DEFAULT FALSE,
    deleted_at   TIMESTAMP    NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_images_product FOREIGN KEY (product_id)
        REFERENCES products (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Create indexes
CREATE INDEX idx_product_ratings_product ON product_ratings (product_id);
CREATE INDEX idx_product_ratings_user ON product_ratings (user_id);
CREATE INDEX idx_product_ratings_deleted ON product_ratings (deleted);
CREATE INDEX idx_images_product ON images (product_id);
CREATE INDEX idx_images_deleted ON images (deleted);

COMMIT;