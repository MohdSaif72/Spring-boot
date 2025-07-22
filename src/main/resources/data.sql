-- Insert sample products
INSERT INTO products (name, description, price, category, stock_quantity, created_date, updated_date) VALUES
('Laptop', 'High-performance laptop for work and gaming', 999.99, 'Electronics', 50, NOW(), NOW()),
('Smartphone', 'Latest smartphone with advanced features', 699.99, 'Electronics', 100, NOW(), NOW()),
('Coffee Maker', 'Automatic coffee maker with timer', 89.99, 'Appliances', 25, NOW(), NOW()),
('Running Shoes', 'Comfortable running shoes for athletes', 129.99, 'Sports', 75, NOW(), NOW()),
('Desk Chair', 'Ergonomic office chair with lumbar support', 199.99, 'Furniture', 30, NOW(), NOW()),
('Headphones', 'Noise-cancelling wireless headphones', 149.99, 'Electronics', 60, NOW(), NOW()),
('Backpack', 'Durable travel backpack with multiple compartments', 59.99, 'Travel', 40, NOW(), NOW()),
('Tablet', '10-inch tablet with high-resolution display', 299.99, 'Electronics', 35, NOW(), NOW()),
('Kitchen Knife Set', 'Professional chef knife set', 79.99, 'Kitchen', 20, NOW(), NOW()),
('Yoga Mat', 'Non-slip yoga mat for exercise', 29.99, 'Sports', 80, NOW(), NOW());

-- Insert admin user (password: admin123)
INSERT INTO customers (first_name, last_name, email, password, phone_number, address, role, created_date) VALUES
('Admin', 'User', 'admin@ecommerce.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '1234567890', '123 Admin Street', 'ADMIN', NOW());

-- Insert sample user (password: user123)
INSERT INTO customers (first_name, last_name, email, password, phone_number, address, role, created_date) VALUES
('John', 'Doe', 'john.doe@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '9876543210', '456 User Avenue', 'USER', NOW());
