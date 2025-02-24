# -- Index for product search by brand and name (frequently used in ProductRepository)
# CREATE INDEX idx_products_brand_name ON products(brand, name);
#
# -- Index for product search by category (used in category-based filtering)
# CREATE INDEX idx_products_category ON products(category_id);
#
# -- Index for product rating queries
# CREATE INDEX idx_products_rating ON products(average_rating, rating_count);
#
# -- Index for order status and date (used in revenue calculations and order tracking)
# CREATE INDEX idx_orders_status_date ON orders(order_status, order_date);
#
# -- Index for user orders (frequently accessed)
# CREATE INDEX idx_orders_user ON orders(user_id, order_date);
#
# -- Index for rating queries by product
# CREATE INDEX idx_ratings_product ON product_ratings(product_id, rating);
#
# -- Index for user rating queries
# CREATE INDEX idx_ratings_user_product ON product_ratings(user_id, product_id);
#
# -- Index for cart item lookups
# CREATE INDEX idx_cart_items_cart ON cart_items(cart_id, product_id);