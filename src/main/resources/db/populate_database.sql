-- Insert data into users table
-- passwords are same as usernames BCrypt encrypted
INSERT INTO users (username, password, email, role)
VALUES ('johndoe', '$2a$10$ztsgjs4PXf9R67IKXnqHLulCgV562Jsmpf6jooj0rHqtIsZSPgV3u', 'johndoe@example.com',
        'CUSTOMER'),
       ('janedoe', '$2a$10$eJL1Gzz3oIf32P4bOdxZA.AAbScHnL4PUKnqo/lOgLEfLW/pq4w..', 'janedoe@example.com',
        'CUSTOMER'),
       ('admin1', '$2a$10$p83hQlItvU7dtzzKe.ZPVOcEDgh1gpj.P3MTGt0OfKyAjl9AyO0s6', 'admin1@example.com',
        'ADMIN'),
       ('admin2', '$2a$10$QLjB88goihyfVS8/5bmR6uPBG7XFXv8sYP/Etn49sJfDnTg6FedUi', 'admin2@example.com',
        'ADMIN'),
       ('susanm', '$2a$10$RhuZvrfC4aA0VjcI8NzHyuHXc/u6IBKQ9Tr7BpFh/PniLodqqT91i', 'susanm@example.com',
        'CUSTOMER'),
       ('michaelb', '$2a$10$DfLKNBGX2z7XKZV1O/6JpeQKVtgNO5MY8xhVksKG.BgrZLKeFQCIC', 'michaelb@example.com',
        'CUSTOMER');

-- Insert data into customers table
INSERT INTO customers (id, last_name, first_name, phone, city, country)
VALUES (1, 'Doe', 'John', '123-456-7890', 'New York', 'USA'),
       (2, 'Doe', 'Jane', '234-567-8901', 'Los Angeles', 'USA'),
       (5, 'Smith', 'Susan', '345-678-9012', 'Chicago', 'USA'),
       (6, 'Brown', 'Michael', '456-789-0123', 'Houston', 'USA');

-- Insert data into admins table
INSERT INTO admins (id)
VALUES (3),
       (4);

-- Insert data into orders table
INSERT INTO orders (customer_id, date, status)
VALUES (1, '2024-08-01', 'Shipped'),
       (2, '2024-08-02', 'Pending'),
       (5, '2024-08-05', 'Pending'),
       (6, '2024-08-06', 'Cancelled'),
       (1, '2024-08-09', 'Pending'),
       (2, '2024-08-10', 'Delivered');

-- Insert data into products table
INSERT INTO products (name, description, stock, price)
VALUES ('Laptop', 'High-performance laptop', 100, 999.99),
       ('Smartphone', 'Latest model smartphone', 150, 499.99),
       ('Headphones', 'Noise-cancelling headphones', 200, 199.99),
       ('Tablet', 'Lightweight tablet', 80, 299.99),
       ('Smartwatch', 'Wearable smartwatch', 120, 149.99),
       ('Camera', 'High-resolution camera', 50, 799.99),
       ('Printer', 'Wireless printer', 60, 129.99),
       ('Monitor', '4K UHD monitor', 75, 349.99),
       ('Keyboard', 'Mechanical keyboard', 90, 89.99),
       ('Mouse', 'Wireless mouse', 110, 49.99);

-- Insert data into order_products table
INSERT INTO order_products (order_id, product_id, quantity)
VALUES (1, 1, 1),
       (1, 2, 2),
       (2, 2, 1),
       (5, 5, 2),
       (6, 6, 1);

-- Insert data into categories table
INSERT INTO categories (name, description)
VALUES ('Electronics', 'Electronic devices and gadgets'),
       ('Accessories', 'Various accessories'),
       ('Computers', 'Computers and peripherals'),
       ('Office Supplies', 'Office supplies and equipment');

-- Insert data into product_categories table
INSERT INTO product_categories (category_id, product_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (1, 6),
       (1, 8),
       (2, 4),
       (2, 5),
       (2, 7),
       (3, 1),
       (3, 4),
       (3, 8),
       (4, 7),
       (4, 9),
       (4, 10);
