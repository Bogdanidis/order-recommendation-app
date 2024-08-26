-- Populate customers table
INSERT INTO customers (last_name, first_name, email, phone, city, country) VALUES
                                       ('Doe', 'John', 'john.doe@example.com', '123-456-7890', 'New York', 'USA'),
                                       ('Smith', 'Jane', 'jane.smith@example.com', '234-567-8901', 'Los Angeles', 'USA'),
                                       ('Brown', 'James', 'james.brown@example.com', '345-678-9012', 'Chicago', 'USA'),
                                       ('Taylor', 'Emily', 'emily.taylor@example.com', '456-789-0123', 'Houston', 'USA'),
                                       ('Anderson', 'Michael', 'michael.anderson@example.com', '567-890-1234', 'Phoenix', 'USA');

-- Populate products table
INSERT INTO products (name, description, stock, price) VALUES
                                       ('Laptop', '15 inch, 16GB RAM', 50, 1200.00),
                                       ('Smartphone', '5.5 inch screen, 64GB storage', 150, 800.00),
                                       ('Headphones', 'Noise-cancelling, over-ear', 200, 150.00),
                                       ('Monitor', '27 inch, 4K resolution', 75, 300.00),
                                       ('Keyboard', 'Mechanical, RGB backlit', 100, 90.00);

-- Populate orders table
INSERT INTO orders (customer_id, date, status) VALUES
                                       (1, '2024-08-20', 'Shipped'),
                                       (2, '2024-08-21', 'Processing'),
                                       (3, '2024-08-22', 'Delivered'),
                                       (4, '2024-08-23', 'Cancelled'),
                                       (5, '2024-08-24', 'Shipped');

-- Populate order_products table
INSERT INTO order_products (order_id, product_id, quantity) VALUES
                                        (1, 1, 1), -- John Doe ordered 1 Laptop
                                        (1, 3, 2), -- John Doe ordered 2 Headphones
                                        (2, 2, 1), -- Jane Smith ordered 1 Smartphone
                                        (3, 4, 2), -- James Brown ordered 2 Monitors
                                        (4, 5, 1), -- Emily Taylor ordered 1 Keyboard
                                        (5, 1, 1), -- Michael Anderson ordered 1 Laptop
                                        (5, 2, 1); -- Michael Anderson ordered 1 Smartphone

-- Populate categories table
INSERT INTO categories (name, description) VALUES
                           ('Electronics', 'Devices like laptops, smartphones, etc.'),
                           ('Accessories', 'Computer and mobile accessories like keyboards, headphones, etc.'),
                           ('Home Office', 'Products suitable for home office setup'),
                           ('Entertainment', 'Products for personal entertainment, like monitors and headphones'),
                           ('Peripherals', 'External devices connected to computers like keyboards and monitors');

-- Populate product_categories table
INSERT INTO product_categories (category_id, product_id) VALUES
                                     (1, 1), -- Laptop categorized as Electronics
                                     (1, 2), -- Smartphone categorized as Electronics
                                     (2, 3), -- Headphones categorized as Accessories
                                     (4, 3), -- Headphones categorized as Entertainment
                                     (4, 4), -- Monitor categorized as Entertainment
                                     (3, 4), -- Monitor categorized as Home Office
                                     (5, 5), -- Keyboard categorized as Peripherals
                                     (2, 5); -- Keyboard categorized as Accessories