-- Insert data into users table
-- passwords are emails BCrypt encrypted
INSERT INTO users (first_name, last_name, email, password)
VALUES
    ('John', 'Doe', 'john.doe@example.com', '$2a$10$cyAwh9X0oVc4PHBT5AqaRuc7N2s6ESZYhLYBGDDcBkXRRH1v1bkDm'),
    ('Jane', 'Smith', 'jane.smith@example.com', '$2a$10$GinnEYhqEIioLLbZ0wgS/uRJA65eyl9uKzFr39ZV8tmWbqJxz9Heq'),
    ('Alice', 'Johnson', 'alice.johnson@example.com', '$2a$10$oLXGZSNxsqcX36JEb9zjUuW2bnI1Lbuy3wscAETpqSowtosQvW4uW'),
    ('Bob', 'Brown', 'bob.brown@example.com', '$2a$10$WavVsND8J6dR6omtHYuNZ.qw7vzecrnX.wjNH9zEfBHE1Nm5SB/cS'),
    ('Charlie', 'Davis', 'charlie.davis@example.com', '$2a$10$9gaLEv3VJyhImuJr2Ub1LOZcblSaiL4v0hIIO4kAZ08lnjcVLgtea'),
    ('Emily', 'Evans', 'emily.evans@example.com', '$2a$10$fAcQemifhv6lhuX0c/gTSOHA0RKo55SofmTIpKhIfKz.JjzG2i4xO');

INSERT INTO categories (name, description)
VALUES
    ('Electronics', 'Devices such as phones, computers, and TVs'),
    ('Appliances', 'Home appliances like washing machines, refrigerators'),
    ('Books', 'Printed books and e-books of various genres'),
    ('Furniture', 'Tables, chairs, and other home furniture'),
    ('Clothing', 'Men and women clothing items'),
    ('Toys', 'Toys for children of all ages');

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
    ('Toy Car', 'Remote control toy car', 100, 29.99, 'Hot Wheels', 6);


INSERT INTO roles (name)
VALUES
    ('ROLE_USER'),
    ('ROLE_ADMIN');

INSERT INTO user_roles (user_id, role_id)
VALUES
    (1, 1), -- John Doe as ADMIN
    (2, 1), -- Jane Smith as USER
    (3, 1), -- Alice Johnson as USER
    (4, 1), -- Bob Brown as USER
    (5, 1), -- Charlie Davis as USER
    (6, 2); -- Emily Evans as ADMIN

INSERT INTO orders (order_date, total_amount, order_status, user_id)
VALUES
    ('2023-10-01', 1049.98, 'SHIPPED', 1),
    ('2023-09-15', 899.99, 'DELIVERED', 2),
    ('2023-09-21', 1299.99, 'PENDING', 3),
    ('2023-10-02', 29.99, 'PROCESSING', 4),
    ('2023-10-03', 1999.99, 'CANCELLED', 5);

INSERT INTO order_items (quantity, price, order_id, product_id)
VALUES
    (1, 999.99, 1, 1), -- 1 iPhone 13 for Order 1
    (1, 49.99, 1, 10), -- 1 Toy Car for Order 1
    (1, 899.99, 2, 2), -- 1 Samsung Galaxy S22 for Order 2
    (1, 19.99, 4, 7), -- 1 Harry Potter book for Order 4
    (1, 1999.99, 5, 3); -- 1 MacBook Pro for Order 5

INSERT INTO carts (total_amount, user_id)
VALUES
    (1049.98, 1), -- Cart for John Doe
    (899.99, 2), -- Cart for Jane Smith
    (1999.99, 3), -- Cart for Alice Johnson
    (19.99, 4), -- Cart for Bob Brown
    (29.99, 5); -- Cart for Charlie Davis

INSERT INTO cart_items (total_price, unit_price, quantity, cart_id, product_id)
VALUES
    (999.99, 999.99, 1, 1, 1), -- 1 iPhone 13 in John Doe's cart
    (49.99, 49.99, 1, 1, 10), -- 1 Toy Car in John Doe's cart
    (899.99, 899.99, 1, 2, 2), -- 1 Samsung Galaxy S22 in Jane Smith's cart
    (19.99, 19.99, 1, 3, 7), -- 1 Harry Potter book in Alice Johnson's cart
    (29.99, 29.99, 1, 5, 10); -- 1 Toy Car in Charlie Davis's cart

INSERT INTO images (file_name, file_type, image, download_url, product_id)
VALUES
    ('iphone13.jpg', 'image/jpeg', LOAD_FILE('path/to/iphone13.jpg'), 'http://example.com/images/iphone13.jpg', 1),
    ('samsung_s22.jpg', 'image/jpeg', LOAD_FILE('path/to/samsung_s22.jpg'), 'http://example.com/images/samsung_s22.jpg', 2),
    ('macbook_pro.jpg', 'image/jpeg', LOAD_FILE('path/to/macbook_pro.jpg'), 'http://example.com/images/macbook_pro.jpg', 3),
    ('sony_tv.jpg', 'image/jpeg', LOAD_FILE('path/to/sony_tv.jpg'), 'http://example.com/images/sony_tv.jpg', 4),
    ('washing_machine.jpg', 'image/jpeg', LOAD_FILE('path/to/washing_machine.jpg'), 'http://example.com/images/washing_machine.jpg', 5);
