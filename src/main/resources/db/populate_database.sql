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

-- Insert data into categories table
INSERT INTO categories (name, description)
VALUES
    ('Laptops', 'Portable personal computers with a built-in screen and keyboard'),
    ('Smartphones', 'Mobile phones with advanced features and computing capabilities'),
    ('Audio', 'Audio devices including headphones and speakers'),
    ('Tablets', 'Touchscreen mobile devices with larger screens'),
    ('Wearables', 'Electronic devices worn on the body, such as smartwatches'),
    ('Cameras', 'Digital cameras for capturing high-quality images and videos'),
    ('Printers', 'Devices for printing documents, photos, and other materials'),
    ('Monitors', 'Computer monitors and display devices'),
    ('Keyboards', 'Input devices used to type text and commands'),
    ('Mice', 'Input devices used to point and click on computer screens');


-- Insert data into products table
INSERT INTO products (name, description, stock, price, brand, category_id)
VALUES
    ('MacBook Pro 14"', 'Apple high-performance 14-inch laptop with M1 chip', 100, 1999.99, 'Apple', 1),           -- Laptops
    ('iPhone 14 Pro', 'Latest Apple iPhone with Pro features', 150, 1099.99, 'Apple', 2),                           -- Smartphones
    ('Corsair Virtuoso RGB Wireless', 'Premium noise-cancelling wireless headphones', 200, 179.99, 'Corsair', 3),   -- Audio
    ('Samsung Galaxy Tab S8', 'Lightweight 11-inch Android tablet with S-Pen', 80, 699.99, 'Samsung', 4),           -- Tablets
    ('Samsung Galaxy Watch 5', 'Wearable smartwatch with fitness tracking', 120, 249.99, 'Samsung', 5),             -- Wearables
    ('Nikon D850', 'High-resolution 45.7 MP DSLR camera', 50, 3299.99, 'Nikon', 6),                                -- Cameras
    ('HP OfficeJet Pro 9015e', 'Wireless all-in-one printer with smart features', 60, 229.99, 'HP', 7),             -- Printers
    ('LG 27UL850-W 27"', '4K UHD 27-inch monitor with USB-C connectivity', 75, 449.99, 'LG', 8),                    -- Monitors
    ('Logitech MX Mechanical', 'Mechanical wireless keyboard with backlighting', 90, 159.99, 'Logitech', 9),        -- Keyboards
    ('Logitech MX Master 3', 'Ergonomic wireless mouse with advanced features', 110, 99.99, 'Logitech', 10);        -- Mice


-- Insert data into order_products table
INSERT INTO order_products (order_id, product_id, quantity)
VALUES (1, 1, 1),
       (1, 2, 2),
       (2, 2, 1),
       (5, 5, 2),
       (6, 6, 1);

