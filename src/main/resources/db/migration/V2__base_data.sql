-- Insert roles (static data)
INSERT INTO roles (name)
VALUES ('ROLE_USER'),
       ('ROLE_ADMIN');

-- Insert admin users (static data)
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

-- Insert base categories (static data)
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
# -- Insert data into users table
# -- passwords are emails BCrypt encrypted
# INSERT INTO users (first_name, last_name, email, password)
# VALUES ('John', 'Doe', 'john.doe@example.com', '$2a$10$4Wakl4/.5RsO6PGcw23KGeXADkmrumtOeGpZfQEB7t8IBJiqjgpVy'),
#        ('Jane', 'Smith', 'jane.smith@example.com',
#         '$2a$10$BR7R3fSpZme8DgNeKHdTbubh.oQMSAnVUisEDrFxKvYo3kqGhDkWW/uRJA65eyl9uKzFr39ZV8tmWbqJxz9Heq'),
#        ('Alice', 'Johnson', 'alice.johnson@example.com',
#         '$2a$10$ASLSCzlHpfnwHNM0ga1Q4OuazaRwr8xjjkAibTQfJRjeukXUuslk2'),
#        ('Bob', 'Brown', 'bob.brown@example.com', '$2a$10$hg0nFbYCwiXpNCv9aQPl/OnWkG5EYj.RTXZWbTVejMWNEJj/K99TG'),
#        ('Charlie', 'Davis', 'charlie.davis@example.com',
#         '$2a$10$eD2k0KYD4pY4OMS0T7CkpeZESOf4q4hKfYnUNUAbHVT7rJU1ripTm'),
#        ('Emily', 'Evans', 'emily.evans@example.com', '$2a$10$dPMv5VeaePI.LxZTkJU0DOV7Mqbjtq62NuN/UIPvZAkBz2cv2myum'),
#        ('David', 'Wilson', 'david.wilson@example.com', '$2a$10$ZODLnPlao4PV0qh0oNBQpe4xEXLoD0da63vAHmvyQ3O8s5wXdtYuC'),
#        ('Sarah', 'Taylor', 'sarah.taylor@example.com', '$2a$10$W4zw4R2sMFeSnL1LH2fIhOgLa00JupwnegFiBwMBnUH3a6io4qjQi'),
#        ('Michael', 'Anderson', 'michael.anderson@example.com',
#         '$2a$10$b9eWYf9s330qx6UpBuuFqO/EEaLcmuEL2omyj8wYfqZBlYUn2sK1O'),
#        ('Emma', 'Thomas', 'emma.thomas@example.com', '$2a$10$O74DpwW5B6xcGJbFUQiQrO6xPOoHB4F/GjjZt9Fk0MuaTdb273nja'),
#        ('Christopher', 'Roberts', 'christopher.roberts@example.com',
#         '$2a$10$fBuv1f7W507stI9xkYxp/OSgyoESn/sxxJ4N2LvHnUM.C3CAyFJaa'),
#        ('Olivia', 'Walker', 'olivia.walker@example.com',
#         '$2a$10$dLqEwoSC3Fg4a9l9WF87Xe6vB7YL34ofbGL1tKCM82D0wCr.1ui/a'),
#        ('Daniel', 'White', 'daniel.white@example.com', '$2a$10$wZuKWK9a3Y4L1UCpdDTQtOwusyXddu8WqvrFLRgwe08YaHnNyhO4a'),
#        ('Sophia', 'Harris', 'sophia.harris@example.com',
#         '$2a$10$O.OA1fxxuqIKbel2XiNldeCtJDSKLp8vB7erFihcYQlh/GrbqNoaq'),
#        ('Matthew', 'Clark', 'matthew.clark@example.com',
#         '$2a$10$YeZMF82l2kX0urxbyRlsveDTmHN4vrC5j9V97AMhh6.SNxfntfnBG');
#
# INSERT INTO categories (name, description)
# VALUES ('Electronics', 'Devices such as phones, computers, and TVs'),
#        ('Appliances', 'Home appliances like washing machines, refrigerators'),
#        ('Books', 'Printed books and e-books of various genres'),
#        ('Furniture', 'Tables, chairs, and other home furniture'),
#        ('Clothing', 'Men and women clothing items'),
#        ('Toys', 'Toys for children of all ages'),
#        ('Sports & Outdoors', 'Equipment and gear for various sports and outdoor activities'),
#        ('Beauty & Personal Care', 'Cosmetics, skincare, and personal grooming products'),
#        ('Home & Garden', 'Decor, gardening tools, and home improvement items'),
#        ('Food & Beverages', 'Groceries, snacks, and drinks'),
#        ('Automotive', 'Car parts, accessories, and maintenance products'),
#        ('Pet Supplies', 'Food, toys, and accessories for pets'),
#        ('Jewelry', 'Necklaces, rings, and other jewelry items'),
#        ('Office Supplies', 'Stationery, desk organizers, and office equipment'),
#        ('Musical Instruments', 'Guitars, pianos, and other musical instruments');
#
# INSERT INTO products (name, description, stock, price, brand, category_id, average_rating, rating_count)
# VALUES
#     ('iPhone 13', 'Latest Apple smartphone', 150, 999.99, 'Apple', 1, 4.5, 2),
#     ('Samsung Galaxy S22', 'Flagship Android smartphone', 100, 899.99, 'Samsung', 1, 4.5, 2),
#     ('MacBook Pro', 'High-performance laptop', 50, 1999.99, 'Apple', 1, 5.0, 2),
#     ('Sony Bravia TV', '4K Ultra HD Smart TV', 40, 799.99, 'Sony', 1, 4.5, 2),
#     ('Washing Machine', 'Front load washing machine', 30, 499.99, 'LG', 2, 4.33, 3),
#     ('Refrigerator', 'Double-door refrigerator', 25, 699.99, 'Whirlpool', 2, 4.5, 2),
#     ('Harry Potter and the Sorcerer''s Stone', 'Fantasy novel by J.K. Rowling', 500, 19.99, 'Scholastic', 3, 0.0, 0),
#     ('Office Chair', 'Ergonomic office chair', 200, 129.99, 'Herman Miller', 4, 4.67, 3),
#     ('Men''s T-shirt', 'Cotton t-shirt', 300, 19.99, 'Nike', 5, 0.0, 0),
#     ('Toy Car', 'Remote control toy car', 100, 29.99, 'Hot Wheels', 6, 0.0, 0),
#     ('Tennis Racket', 'Professional grade tennis racket', 50, 199.99, 'Wilson', 7, 0.0, 0),
#     ('Yoga Mat', 'Non-slip exercise yoga mat', 100, 29.99, 'Lululemon', 7, 0.0, 0),
#     ('Lipstick', 'Long-lasting matte lipstick', 200, 24.99, 'MAC', 8, 0.0, 0),
#     ('Electric Shaver', 'Rechargeable electric shaver', 75, 89.99, 'Philips', 8, 0.0, 0),
#     ('Throw Pillow Set', 'Decorative throw pillow set of 4', 150, 39.99, 'HomeGoods', 9, 0.0, 0),
#     ('Garden Hose', '50ft expandable garden hose', 80, 34.99, 'Flexzilla', 9, 0.0, 0),
#     ('Organic Coffee Beans', 'Fair trade organic coffee beans', 300, 14.99, 'Starbucks', 10, 0.0, 0),
#     ('Protein Bar Variety Pack', 'Assorted flavors protein bars', 500, 19.99, 'KIND', 10, 0.0, 0),
#     ('Car Phone Mount', 'Universal car phone holder', 200, 19.99, 'iOttie', 11, 0.0, 0),
#     ('Motor Oil', 'Synthetic blend motor oil', 400, 24.99, 'Mobil 1', 11, 0.0, 0),
#     ('Dog Food', 'Premium dry dog food', 100, 49.99, 'Royal Canin', 12, 0.0, 0),
#     ('Cat Litter', 'Clumping cat litter', 150, 19.99, 'Fresh Step', 12, 0.0, 0),
#     ('Diamond Necklace', '14k gold diamond pendant necklace', 20, 999.99, 'Tiffany & Co.', 13, 0.0, 0),
#     ('Printer Paper', 'Multipurpose printer paper, 500 sheets', 1000, 9.99, 'HP', 14, 0.0, 0),
#     ('Acoustic Guitar', 'Beginner acoustic guitar', 30, 199.99, 'Yamaha', 15, 0.0, 0),
#     ('Digital Piano', '88-key weighted digital piano', 15, 799.99, 'Casio', 15, 0.0, 0),
#     ('Microwave Oven', 'Countertop microwave oven', 50, 89.99, 'Panasonic', 2, 0.0, 0),
#     ('The Great Gatsby', 'Classic novel by F. Scott Fitzgerald', 300, 12.99, 'Scribner', 3, 0.0, 0),
#     ('Dining Table', '6-seater wooden dining table', 30, 399.99, 'IKEA', 4, 0.0, 0),
#     ('Women''s Dress', 'Floral print summer dress', 100, 59.99, 'Zara', 5, 0.0, 0);
#
# INSERT INTO roles (name)
# VALUES ('ROLE_USER'),
#        ('ROLE_ADMIN');
#
# INSERT INTO user_roles (user_id, role_id)
# VALUES (1, 1),  -- John Doe as USER
#        (2, 1),  -- Jane Smith as USER
#        (3, 1),  -- Alice Johnson as USER
#        (4, 1),  -- Bob Brown as USER
#        (5, 1),  -- Charlie Davis as USER
#        (6, 2),  -- Emily Evans as ADMIN
#        (7, 1),  -- David Wilson as USER
#        (8, 1),  -- Sarah Taylor as USER
#        (9, 1),  -- Michael Anderson as USER
#        (10, 1), -- Emma Thomas as USER
#        (11, 1), -- Christopher Roberts as USER
#        (12, 1), -- Olivia Walker as USER
#        (13, 1), -- Daniel White as USER
#        (14, 1), -- Sophia Harris as USER
#        (15, 2);
# -- Matthew Clark as ADMIN
#
# -- Insert more orders
# INSERT INTO orders (order_date, total_amount, order_status, user_id)
# VALUES
#     -- January Week 1 Orders (Users 1-5)
#     ('2024-01-01', 1299.98, 'DELIVERED', 1),   -- Order 1: iPhone 13 (999.99) + Office Chair (299.99)
#     ('2024-01-01', 899.99, 'DELIVERED', 2),    -- Order 2: Samsung Galaxy S22
#     ('2024-01-02', 2499.98, 'DELIVERED', 3),   -- Order 3: MacBook Pro (1999.99) + Washing Machine (499.99)
#     ('2024-01-02', 1599.98, 'DELIVERED', 4),   -- Order 4: Sony TV (799.99) + Digital Piano (799.99)
#     ('2024-01-03', 799.99, 'DELIVERED', 5),    -- Order 5: Sony TV
#
#     -- January Week 2 Orders (Users 7-11)
#     ('2024-01-08', 699.99, 'DELIVERED', 7),    -- Order 6: Refrigerator
#     ('2024-01-08', 299.98, 'DELIVERED', 8),    -- Order 7: Office Chair x2 (149.99 each)
#     ('2024-01-09', 449.99, 'DELIVERED', 9),    -- Order 8: Washing Machine
#     ('2024-01-09', 199.99, 'DELIVERED', 10),   -- Order 9: Acoustic Guitar
#     ('2024-01-10', 999.99, 'DELIVERED', 11),   -- Order 10: iPhone 13
#
#     -- January Week 3 Orders (Users 12-14, 1-3)
#     ('2024-01-15', 2499.98, 'DELIVERED', 12),  -- Order 11: MacBook Pro + Washing Machine
#     ('2024-01-15', 899.99, 'DELIVERED', 13),   -- Order 12: Samsung Galaxy S22
#     ('2024-01-16', 699.99, 'DELIVERED', 14),   -- Order 13: Refrigerator
#     ('2024-01-16', 399.99, 'DELIVERED', 1),    -- Order 14: Dining Table
#     ('2024-01-17', 299.99, 'DELIVERED', 2),    -- Order 15: Office Chair
#
#     -- Recent Orders (Mixed Users)
#     ('2024-01-18', 199.99, 'SHIPPED', 3),      -- Order 16: Acoustic Guitar
#     ('2024-01-18', 149.99, 'SHIPPED', 4),      -- Order 17: Tennis Racket
#     ('2024-01-19', 999.99, 'PROCESSING', 5),   -- Order 18: iPhone 13
#     ('2024-01-19', 799.99, 'PROCESSING', 7),   -- Order 19: Digital Piano
#     ('2024-01-20', 499.99, 'PENDING', 8),      -- Order 20: Washing Machine
#
#     -- Latest Orders
#     ('2024-01-21', 399.99, 'PROCESSING', 9),   -- Order 21: Dining Table
#     ('2024-01-21', 299.99, 'PROCESSING', 10),  -- Order 22: Office Chair
#     ('2024-01-22', 199.99, 'PENDING', 11),     -- Order 23: Tennis Racket
#     ('2024-01-22', 1999.99, 'PENDING', 12),    -- Order 24: MacBook Pro
#     ('2024-01-23', 899.99, 'PENDING', 13);     -- Order 25: Samsung Galaxy S22
#
# -- Insert more order items
# INSERT INTO order_items (quantity, price, order_id, product_id)
# VALUES
#     -- Week 1 Order Items
#     (1, 999.99, 1, 1),    -- iPhone 13 for Order 1
#     (1, 299.99, 1, 8),    -- Office Chair for Order 1
#     (1, 899.99, 2, 2),    -- Samsung Galaxy S22 for Order 2
#     (1, 1999.99, 3, 3),   -- MacBook Pro for Order 3
#     (1, 499.99, 3, 5),    -- Washing Machine for Order 3
#     (1, 799.99, 4, 4),    -- Sony TV for Order 4
#     (1, 799.99, 4, 26),   -- Digital Piano for Order 4
#     (1, 799.99, 5, 4),    -- Sony TV for Order 5
#
#     -- Week 2 Order Items
#     (1, 699.99, 6, 6),    -- Refrigerator for Order 6
#     (2, 149.99, 7, 8),    -- Office Chair x2 for Order 7
#     (1, 449.99, 8, 5),    -- Washing Machine for Order 8
#     (1, 199.99, 9, 25),   -- Acoustic Guitar for Order 9
#     (1, 999.99, 10, 1),   -- iPhone 13 for Order 10
#
#     -- Week 3 Order Items
#     (1, 1999.99, 11, 3),  -- MacBook Pro for Order 11
#     (1, 499.99, 11, 5),   -- Washing Machine for Order 11
#     (1, 899.99, 12, 2),   -- Samsung Galaxy S22 for Order 12
#     (1, 699.99, 13, 6),   -- Refrigerator for Order 13
#     (1, 399.99, 14, 29),  -- Dining Table for Order 14
#     (1, 299.99, 15, 8),   -- Office Chair for Order 15
#
#     -- Recent Order Items
#     (1, 199.99, 16, 25),  -- Acoustic Guitar for Order 16
#     (1, 149.99, 17, 11),  -- Tennis Racket for Order 17
#     (1, 999.99, 18, 1),   -- iPhone 13 for Order 18
#     (1, 799.99, 19, 26),  -- Digital Piano for Order 19
#     (1, 499.99, 20, 5),   -- Washing Machine for Order 20
#
#     -- Latest Order Items
#     (1, 399.99, 21, 29),  -- Dining Table for Order 21
#     (1, 299.99, 22, 8),   -- Office Chair for Order 22
#     (1, 199.99, 23, 11),  -- Tennis Racket for Order 23
#     (1, 1999.99, 24, 3),  -- MacBook Pro for Order 24
#     (1, 899.99, 25, 2);   -- Samsung Galaxy S22 for Order 25
#
#
#
# -- Insert sample product ratings
# INSERT INTO product_ratings (rating, comment, created_at, product_id, user_id)
# VALUES
#     -- iPhone 13 ratings (Product 1)
#     (5, 'Exceptional phone! Camera quality is outstanding.', '2024-01-10', 1, 1),      -- From order 28
#     (4, 'Great device overall. Battery life is impressive.', '2024-01-15', 1, 11),     -- From order 37
#
#     -- Samsung Galaxy S22 ratings (Product 2)
#     (4, 'Excellent Android experience. Great customization options.', '2024-01-05', 2, 2),   -- From order 29
#     (5, 'Amazing camera capabilities and beautiful display.', '2024-01-17', 2, 13),          -- From order 39
#
#     -- MacBook Pro ratings (Product 3)
#     (5, 'Incredible machine for development work.', '2024-01-07', 3, 3),              -- From order 30
#     (5, 'Outstanding performance and battery life is unreal.', '2024-01-17', 3, 12),  -- From order 38
#
#     -- Sony Bravia TV ratings (Product 4)
#     (5, 'Stunning picture quality! Perfect for movies and gaming.', '2024-01-07', 4, 4),  -- From order 31
#     (4, 'Great TV with excellent smart features.', '2024-01-08', 4, 5),                   -- From order 32
#
#     -- Washing Machine ratings (Product 5)
#     (4, 'Energy efficient and cleans thoroughly.', '2024-01-07', 5, 3),              -- From order 30
#     (5, 'Very quiet operation and excellent cleaning power.', '2024-01-17', 5, 12),  -- From order 38
#     (4, 'Good value for money, many useful features.', '2024-01-12', 5, 9),         -- From order 35
#
#     -- Refrigerator ratings (Product 6)
#     (4, 'Spacious and energy efficient. Good temperature control.', '2024-01-12', 6, 7),  -- From order 33
#     (5, 'Perfect size and great organization options.', '2024-01-18', 6, 14),             -- From order 40
#
#     -- Office Chair ratings (Product 8)
#     (5, 'Extremely comfortable for long working hours.', '2024-01-05', 8, 1),     -- From order 28
#     (4, 'Good ergonomic support and adjustability.', '2024-01-12', 8, 8),         -- From order 34
#     (5, 'Best office chair I''ve ever used.', '2024-01-18', 8, 2);                -- From order 42
#
# -- Update products
# # INSERT INTO carts (total_amount, user_id)
# # VALUES
# #     (1049.98, 1), -- Cart for John Doe
# #     (899.99, 2), -- Cart for Jane Smith
# #     (1999.99, 3), -- Cart for Alice Johnson
# #     (19.99, 4), -- Cart for Bob Brown
# #     (29.99, 5); -- Cart for Charlie Davis
# #
# # INSERT INTO cart_items (total_price, unit_price, quantity, cart_id, product_id)
# # VALUES
# #     (999.99, 999.99, 1, 1, 1), -- 1 iPhone 13 in John Doe's cart
# #     (49.99, 49.99, 1, 1, 10), -- 1 Toy Car in John Doe's cart
# #     (899.99, 899.99, 1, 2, 2), -- 1 Samsung Galaxy S22 in Jane Smith's cart
# #     (19.99, 19.99, 1, 3, 7), -- 1 Harry Potter book in Alice Johnson's cart
# #     (29.99, 29.99, 1, 5, 10); -- 1 Toy Car in Charlie Davis's cart
# #
# # INSERT INTO images (file_name, file_type, image, download_url, product_id)
# # VALUES
# #     ('iphone13.jpg', 'image/jpeg', LOAD_FILE('path/to/iphone13.jpg'), 'http://example.com/images/iphone13.jpg', 1),
# #     ('samsung_s22.jpg', 'image/jpeg', LOAD_FILE('path/to/samsung_s22.jpg'), 'http://example.com/images/samsung_s22.jpg', 2),
# #     ('macbook_pro.jpg', 'image/jpeg', LOAD_FILE('path/to/macbook_pro.jpg'), 'http://example.com/images/macbook_pro.jpg', 3),
# #     ('sony_tv.jpg', 'image/jpeg', LOAD_FILE('path/to/sony_tv.jpg'), 'http://example.com/images/sony_tv.jpg', 4),
# #     ('washing_machine.jpg', 'image/jpeg', LOAD_FILE('path/to/washing_machine.jpg'), 'http://example.com/images/washing_machine.jpg', 5);
