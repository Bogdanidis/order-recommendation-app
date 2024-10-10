-- Insert data into users table
-- passwords are emails BCrypt encrypted
INSERT INTO users (first_name, last_name, email, password)
VALUES
    ('John', 'Doe', 'john.doe@example.com', '$2a$10$4Wakl4/.5RsO6PGcw23KGeXADkmrumtOeGpZfQEB7t8IBJiqjgpVy'),
    ('Jane', 'Smith', 'jane.smith@example.com', '$2a$10$BR7R3fSpZme8DgNeKHdTbubh.oQMSAnVUisEDrFxKvYo3kqGhDkWW/uRJA65eyl9uKzFr39ZV8tmWbqJxz9Heq'),
    ('Alice', 'Johnson', 'alice.johnson@example.com', '$2a$10$ASLSCzlHpfnwHNM0ga1Q4OuazaRwr8xjjkAibTQfJRjeukXUuslk2'),
    ('Bob', 'Brown', 'bob.brown@example.com', '$2a$10$hg0nFbYCwiXpNCv9aQPl/OnWkG5EYj.RTXZWbTVejMWNEJj/K99TG'),
    ('Charlie', 'Davis', 'charlie.davis@example.com', '$2a$10$eD2k0KYD4pY4OMS0T7CkpeZESOf4q4hKfYnUNUAbHVT7rJU1ripTm'),
    ('Emily', 'Evans', 'emily.evans@example.com', '$2a$10$dPMv5VeaePI.LxZTkJU0DOV7Mqbjtq62NuN/UIPvZAkBz2cv2myum'),
    ('David', 'Wilson', 'david.wilson@example.com', '$2a$10$ZODLnPlao4PV0qh0oNBQpe4xEXLoD0da63vAHmvyQ3O8s5wXdtYuC'),
    ('Sarah', 'Taylor', 'sarah.taylor@example.com', '$2a$10$W4zw4R2sMFeSnL1LH2fIhOgLa00JupwnegFiBwMBnUH3a6io4qjQi'),
    ('Michael', 'Anderson', 'michael.anderson@example.com', '$2a$10$b9eWYf9s330qx6UpBuuFqO/EEaLcmuEL2omyj8wYfqZBlYUn2sK1O'),
    ('Emma', 'Thomas', 'emma.thomas@example.com', '$2a$10$O74DpwW5B6xcGJbFUQiQrO6xPOoHB4F/GjjZt9Fk0MuaTdb273nja'),
    ('Christopher', 'Roberts', 'christopher.roberts@example.com', '$2a$10$fBuv1f7W507stI9xkYxp/OSgyoESn/sxxJ4N2LvHnUM.C3CAyFJaa'),
    ('Olivia', 'Walker', 'olivia.walker@example.com', '$2a$10$dLqEwoSC3Fg4a9l9WF87Xe6vB7YL34ofbGL1tKCM82D0wCr.1ui/a'),
    ('Daniel', 'White', 'daniel.white@example.com', '$2a$10$wZuKWK9a3Y4L1UCpdDTQtOwusyXddu8WqvrFLRgwe08YaHnNyhO4a'),
    ('Sophia', 'Harris', 'sophia.harris@example.com', '$2a$10$O.OA1fxxuqIKbel2XiNldeCtJDSKLp8vB7erFihcYQlh/GrbqNoaq'),
    ('Matthew', 'Clark', 'matthew.clark@example.com', '$2a$10$YeZMF82l2kX0urxbyRlsveDTmHN4vrC5j9V97AMhh6.SNxfntfnBG');

INSERT INTO categories (name, description)
VALUES
    ('Electronics', 'Devices such as phones, computers, and TVs'),
    ('Appliances', 'Home appliances like washing machines, refrigerators'),
    ('Books', 'Printed books and e-books of various genres'),
    ('Furniture', 'Tables, chairs, and other home furniture'),
    ('Clothing', 'Men and women clothing items'),
    ('Toys', 'Toys for children of all ages'),
    ('Sports & Outdoors', 'Equipment and gear for various sports and outdoor activities'),
    ('Beauty & Personal Care', 'Cosmetics, skincare, and personal grooming products'),
    ('Home & Garden', 'Decor, gardening tools, and home improvement items'),
    ('Food & Beverages', 'Groceries, snacks, and drinks'),
    ('Automotive', 'Car parts, accessories, and maintenance products'),
    ('Pet Supplies', 'Food, toys, and accessories for pets'),
    ('Jewelry', 'Necklaces, rings, and other jewelry items'),
    ('Office Supplies', 'Stationery, desk organizers, and office equipment'),
    ('Musical Instruments', 'Guitars, pianos, and other musical instruments');

INSERT INTO products (name, description, stock, price, brand, category_id)
VALUES
    ('iPhone 13', 'Latest Apple smartphone', 150, 999.99, 'Apple', 1),
    ('Samsung Galaxy S22', 'Flagship Android smartphone', 100, 899.99, 'Samsung', 1),
    ('MacBook Pro', 'High-performance laptop', 50, 1999.99, 'Apple', 1),
    ('Sony Bravia TV', '4K Ultra HD Smart TV', 40, 799.99, 'Sony', 1),
    ('Washing Machine', 'Front load washing machine', 30, 499.99, 'LG', 2),
    ('Refrigerator', 'Double-door refrigerator', 25, 699.99, 'Whirlpool', 2),
    ('Harry Potter and the Sorcerer\'s Stone', 'Fantasy novel by J.K. Rowling', 500, 19.99, 'Scholastic', 3),
    ('Office Chair', 'Ergonomic office chair', 200, 129.99, 'Herman Miller', 4),
    ('Men\'s T-shirt', 'Cotton t-shirt', 300, 19.99, 'Nike', 5),
    ('Toy Car', 'Remote control toy car', 100, 29.99, 'Hot Wheels', 6),
    ('Tennis Racket', 'Professional grade tennis racket', 50, 199.99, 'Wilson', 7),
    ('Yoga Mat', 'Non-slip exercise yoga mat', 100, 29.99, 'Lululemon', 7),
    ('Lipstick', 'Long-lasting matte lipstick', 200, 24.99, 'MAC', 8),
    ('Electric Shaver', 'Rechargeable electric shaver', 75, 89.99, 'Philips', 8),
    ('Throw Pillow Set', 'Decorative throw pillow set of 4', 150, 39.99, 'HomeGoods', 9),
    ('Garden Hose', '50ft expandable garden hose', 80, 34.99, 'Flexzilla', 9),
    ('Organic Coffee Beans', 'Fair trade organic coffee beans', 300, 14.99, 'Starbucks', 10),
    ('Protein Bar Variety Pack', 'Assorted flavors protein bars', 500, 19.99, 'KIND', 10),
    ('Car Phone Mount', 'Universal car phone holder', 200, 19.99, 'iOttie', 11),
    ('Motor Oil', 'Synthetic blend motor oil', 400, 24.99, 'Mobil 1', 11),
    ('Dog Food', 'Premium dry dog food', 100, 49.99, 'Royal Canin', 12),
    ('Cat Litter', 'Clumping cat litter', 150, 19.99, 'Fresh Step', 12),
    ('Diamond Necklace', '14k gold diamond pendant necklace', 20, 999.99, 'Tiffany & Co.', 13),
    ('Printer Paper', 'Multipurpose printer paper, 500 sheets', 1000, 9.99, 'HP', 14),
    ('Acoustic Guitar', 'Beginner acoustic guitar', 30, 199.99, 'Yamaha', 15),
    ('Digital Piano', '88-key weighted digital piano', 15, 799.99, 'Casio', 15),
    ('Microwave Oven', 'Countertop microwave oven', 50, 89.99, 'Panasonic', 2),
    ('The Great Gatsby', 'Classic novel by F. Scott Fitzgerald', 300, 12.99, 'Scribner', 3),
    ('Dining Table', '6-seater wooden dining table', 30, 399.99, 'IKEA', 4),
    ('Women\'s Dress', 'Floral print summer dress', 100, 59.99, 'Zara', 5);

INSERT INTO roles (name)
VALUES
    ('ROLE_USER'),
    ('ROLE_ADMIN');

INSERT INTO user_roles (user_id, role_id)
VALUES
    (1, 1), -- John Doe as USER
    (2, 1), -- Jane Smith as USER
    (3, 1), -- Alice Johnson as USER
    (4, 1), -- Bob Brown as USER
    (5, 1), -- Charlie Davis as USER
    (6, 2), -- Emily Evans as ADMIN
    (7, 1), -- David Wilson as USER
    (8, 1), -- Sarah Taylor as USER
    (9, 1), -- Michael Anderson as USER
    (10, 1), -- Emma Thomas as USER
    (11, 1), -- Christopher Roberts as USER
    (12, 1), -- Olivia Walker as USER
    (13, 1), -- Daniel White as USER
    (14, 1), -- Sophia Harris as USER
    (15, 2); -- Matthew Clark as ADMIN

-- Insert more orders
INSERT INTO orders (order_date, total_amount, order_status, user_id)
VALUES
    ('2023-10-01', 1049.98, 'SHIPPED', 1),
    ('2023-10-03', 2199.98, 'DELIVERED', 1),
    ('2023-10-05', 349.97, 'CANCELLED', 1),
    ('2023-09-15', 899.99, 'DELIVERED', 2),
    ('2023-09-20', 1299.98, 'SHIPPED', 2),
    ('2023-09-21', 1299.99, 'PENDING', 3),
    ('2023-09-25', 449.97, 'PROCESSING', 3),
    ('2023-10-02', 29.99, 'PROCESSING', 4),
    ('2023-10-04', 699.99, 'SHIPPED', 4),
    ('2023-10-03', 1999.99, 'CANCELLED', 5),
    ('2023-10-06', 149.97, 'PENDING', 5),
    ('2023-10-07', 89.99, 'PROCESSING', 7),
    ('2023-10-09', 1099.98, 'SHIPPED', 7),
    ('2023-10-08', 399.99, 'PENDING', 8),
    ('2023-10-11', 79.98, 'PROCESSING', 8),
    ('2023-10-10', 59.99, 'SHIPPED', 9),
    ('2023-10-13', 1499.97, 'DELIVERED', 9),
    ('2023-10-12', 199.99, 'DELIVERED', 10),
    ('2023-10-15', 549.97, 'SHIPPED', 10),
    ('2023-10-03', 1999.99, 'CANCELLED', 11),
    ('2023-10-07', 299.97, 'PENDING', 11),
    ('2023-10-05', 249.98, 'SHIPPED', 12),
    ('2023-10-09', 799.98, 'PROCESSING', 12),
    ('2023-10-07', 89.99, 'PROCESSING', 13),
    ('2023-10-11', 1299.97, 'SHIPPED', 13),
    ('2023-10-08', 399.99, 'PENDING', 14),
    ('2023-10-12', 99.98, 'DELIVERED', 14);


-- Insert more order items
INSERT INTO order_items (quantity, price, order_id, product_id)
VALUES
    (1, 999.99, 1, 1),  -- 1 iPhone 13 for Order 1
    (1, 49.99, 1, 10),  -- 1 Toy Car for Order 1
    (1, 1999.99, 2, 3), -- 1 MacBook Pro for Order 2
    (1, 199.99, 2, 11), -- 1 Tennis Racket for Order 2
    (3, 99.99, 3, 9),   -- 3 Men's T-shirts for Order 3
    (1, 49.99, 3, 21),  -- 1 Dog Food for Order 3
    (1, 899.99, 4, 2),  -- 1 Samsung Galaxy S22 for Order 4
    (2, 599.99, 5, 4),  -- 2 Sony Bravia TVs for Order 5
    (1, 99.99, 5, 8),   -- 1 Office Chair for Order 5
    (1, 1299.99, 6, 3), -- 1 MacBook Pro for Order 6
    (3, 129.99, 7, 8),  -- 3 Office Chairs for Order 7
    (1, 59.99, 7, 30),  -- 1 Women's Dress for Order 7
    (1, 29.99, 8, 10),  -- 1 Toy Car for Order 8
    (1, 699.99, 9, 6),  -- 1 Refrigerator for Order 9
    (1, 1999.99, 10, 3),-- 1 MacBook Pro for Order 10
    (3, 39.99, 11, 15), -- 3 Throw Pillow Sets for Order 11
    (1, 29.99, 11, 12), -- 1 Yoga Mat for Order 11
    (1, 89.99, 12, 14), -- 1 Electric Shaver for Order 12
    (1, 999.99, 13, 1), -- 1 iPhone 13 for Order 13
    (1, 99.99, 13, 8),  -- 1 Office Chair for Order 13
    (1, 399.99, 14, 29),-- 1 Dining Table for Order 14
    (2, 39.99, 15, 15), -- 2 Throw Pillow Sets for Order 15
    (1, 59.99, 16, 30), -- 1 Women's Dress for Order 16
    (3, 499.99, 17, 5), -- 3 Washing Machines for Order 17
    (1, 199.99, 18, 25),-- 1 Acoustic Guitar for Order 18
    (2, 159.99, 19, 26),-- 2 Digital Pianos for Order 19
    (1, 229.99, 19, 27),-- 1 Microwave Oven for Order 19
    (1, 1999.99, 20, 3),-- 1 MacBook Pro for Order 20
    (3, 79.99, 21, 28), -- 3 The Great Gatsby books for Order 21
    (1, 59.99, 21, 30), -- 1 Women's Dress for Order 21
    (1, 249.98, 22, 25),-- 1 Acoustic Guitar for Order 22
    (2, 349.99, 23, 29),-- 2 Dining Tables for Order 23
    (1, 99.99, 23, 8),  -- 1 Office Chair for Order 23
    (1, 89.99, 24, 14), -- 1 Electric Shaver for Order 24
    (1, 1199.99, 25, 1),-- 1 iPhone 13 for Order 25
    (1, 99.98, 25, 28), -- 1 The Great Gatsby book for Order 25
    (1, 399.99, 26, 29),-- 1 Dining Table for Order 26
    (2, 49.99, 27, 21); -- 2 Dog Food for Order 27


# INSERT INTO carts (total_amount, user_id)
# VALUES
#     (1049.98, 1), -- Cart for John Doe
#     (899.99, 2), -- Cart for Jane Smith
#     (1999.99, 3), -- Cart for Alice Johnson
#     (19.99, 4), -- Cart for Bob Brown
#     (29.99, 5); -- Cart for Charlie Davis
#
# INSERT INTO cart_items (total_price, unit_price, quantity, cart_id, product_id)
# VALUES
#     (999.99, 999.99, 1, 1, 1), -- 1 iPhone 13 in John Doe's cart
#     (49.99, 49.99, 1, 1, 10), -- 1 Toy Car in John Doe's cart
#     (899.99, 899.99, 1, 2, 2), -- 1 Samsung Galaxy S22 in Jane Smith's cart
#     (19.99, 19.99, 1, 3, 7), -- 1 Harry Potter book in Alice Johnson's cart
#     (29.99, 29.99, 1, 5, 10); -- 1 Toy Car in Charlie Davis's cart
#
# INSERT INTO images (file_name, file_type, image, download_url, product_id)
# VALUES
#     ('iphone13.jpg', 'image/jpeg', LOAD_FILE('path/to/iphone13.jpg'), 'http://example.com/images/iphone13.jpg', 1),
#     ('samsung_s22.jpg', 'image/jpeg', LOAD_FILE('path/to/samsung_s22.jpg'), 'http://example.com/images/samsung_s22.jpg', 2),
#     ('macbook_pro.jpg', 'image/jpeg', LOAD_FILE('path/to/macbook_pro.jpg'), 'http://example.com/images/macbook_pro.jpg', 3),
#     ('sony_tv.jpg', 'image/jpeg', LOAD_FILE('path/to/sony_tv.jpg'), 'http://example.com/images/sony_tv.jpg', 4),
#     ('washing_machine.jpg', 'image/jpeg', LOAD_FILE('path/to/washing_machine.jpg'), 'http://example.com/images/washing_machine.jpg', 5);
