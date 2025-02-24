/*
 * V2.0 - Base data
 * Inserts required initial data (roles, admin users, base categories)
 */

SET autocommit = 0;
START TRANSACTION;

-- Insert roles
INSERT INTO roles (name)
VALUES ('ROLE_USER'),
       ('ROLE_ADMIN');

-- Insert admin users with BCrypt encoded passwords
-- admin@example.com / admin123
-- system@example.com / system123
INSERT INTO users (first_name, last_name, email, password)
VALUES ('Admin', 'User', 'admin@example.com', '$2a$10$XEOFJf7s9X/IdQtr2BwuwuNuEMFJXQO6V0JAjkCmdnXKw8hP7sB8K'),
       ('System', 'Admin', 'system@example.com', '$2a$10$ZDUCiDaQ5HJOnDhytO28NeVshP03uZBZPbbynxp1kuWK58K4mo64m');

-- Assign admin roles
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u,
     roles r
WHERE r.name = 'ROLE_ADMIN'
  AND u.email IN ('admin@example.com', 'system@example.com');

-- Insert base categories
INSERT INTO categories (name, description)
VALUES ('Electronics', 'Latest gadgets and electronic devices'),
       ('Clothing', 'Fashion items and accessories'),
       ('Books', 'Literature and educational materials'),
       ('Home & Kitchen', 'Home essentials and appliances'),
       ('Sports', 'Sports equipment and accessories'),
       ('Beauty', 'Cosmetics and personal care'),
       ('Toys', 'Educational and recreational toys'),
       ('Automotive', 'Car parts and accessories'),
       ('Health', 'Health and wellness products'),
       ('Garden', 'Gardening tools and supplies'),
       ('Office', 'Office supplies and equipment'),
       ('Pet Supplies', 'Products for pets');

COMMIT;