# -- Core entities
# ALTER TABLE users ADD COLUMN deleted BOOLEAN DEFAULT FALSE;
# ALTER TABLE users ADD COLUMN deleted_at TIMESTAMP;
#
# ALTER TABLE products ADD COLUMN deleted BOOLEAN DEFAULT FALSE;
# ALTER TABLE products ADD COLUMN deleted_at TIMESTAMP;
#
# ALTER TABLE categories ADD COLUMN deleted BOOLEAN DEFAULT FALSE;
# ALTER TABLE categories ADD COLUMN deleted_at TIMESTAMP;
#
# ALTER TABLE orders ADD COLUMN deleted BOOLEAN DEFAULT FALSE;
# ALTER TABLE orders ADD COLUMN deleted_at TIMESTAMP;
#
# -- Supporting entities
# ALTER TABLE product_ratings ADD COLUMN deleted BOOLEAN DEFAULT FALSE;
# ALTER TABLE product_ratings ADD COLUMN deleted_at TIMESTAMP;
#
# ALTER TABLE images ADD COLUMN deleted BOOLEAN DEFAULT FALSE;
# ALTER TABLE images ADD COLUMN deleted_at TIMESTAMP;
#
# -- Create indexes for improved query performance
# CREATE INDEX idx_users_deleted ON users(deleted);
# CREATE INDEX idx_products_deleted ON products(deleted);
# CREATE INDEX idx_categories_deleted ON categories(deleted);
# CREATE INDEX idx_orders_deleted ON orders(deleted);
# CREATE INDEX idx_product_ratings_deleted ON product_ratings(deleted);
# CREATE INDEX idx_images_deleted ON images(deleted);