-- Create database
CREATE DATABASE IF NOT EXISTS ecommerce_db;
USE ecommerce_db;

-- Create products table
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(255) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    created_date DATETIME NOT NULL,
    updated_date DATETIME NOT NULL,
    INDEX idx_category (category),
    INDEX idx_name (name),
    INDEX idx_stock (stock_quantity)
);

-- Create customers table
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    address TEXT,
    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    created_date DATETIME NOT NULL,
    INDEX idx_email (email),
    INDEX idx_role (role)
);

-- Create orders table
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    order_date DATETIME NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status),
    INDEX idx_order_date (order_date)
);

-- Create order_items table
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id)
);

-- Insert sample products
INSERT INTO products (name, description, price, category, stock_quantity, created_date, updated_date) VALUES
('Gaming Laptop', 'High-performance gaming laptop with RTX 4060', 1299.99, 'Electronics', 15, NOW(), NOW()),
('iPhone 15 Pro', 'Latest iPhone with A17 Pro chip', 999.99, 'Electronics', 50, NOW(), NOW()),
('Espresso Machine', 'Professional espresso machine for home use', 599.99, 'Appliances', 8, NOW(), NOW()),
('Nike Air Max', 'Comfortable running shoes with air cushioning', 149.99, 'Sports', 100, NOW(), NOW()),
('Herman Miller Chair', 'Ergonomic office chair with 12-year warranty', 1395.00, 'Furniture', 5, NOW(), NOW()),
('Sony WH-1000XM5', 'Industry-leading noise cancelling headphones', 399.99, 'Electronics', 25, NOW(), NOW()),
('Patagonia Backpack', 'Sustainable hiking backpack 30L', 89.99, 'Travel', 30, NOW(), NOW()),
('iPad Pro 12.9', 'M2 chip tablet with Liquid Retina display', 1099.99, 'Electronics', 20, NOW(), NOW()),
('Wusthof Classic', 'German steel chef knife set', 299.99, 'Kitchen', 12, NOW(), NOW()),
('Manduka Pro Mat', 'Professional yoga mat 6mm thickness', 79.99, 'Sports', 40, NOW(), NOW()),
('Samsung 4K TV', '55-inch QLED smart TV with HDR', 899.99, 'Electronics', 18, NOW(), NOW()),
('KitchenAid Mixer', 'Stand mixer with multiple attachments', 449.99, 'Appliances', 22, NOW(), NOW()),
('Adidas Ultraboost', 'Energy-returning running shoes', 189.99, 'Sports', 75, NOW(), NOW()),
('IKEA Desk', 'Adjustable height standing desk', 299.99, 'Furniture', 35, NOW(), NOW()),
('Instant Pot', '8-quart multi-use pressure cooker', 129.99, 'Appliances', 45, NOW(), NOW());

-- Insert admin user (password: admin123 - BCrypt encoded)
INSERT INTO customers (first_name, last_name, email, password, phone_number, address, role, created_date) VALUES
('Admin', 'User', 'admin@ecommerce.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '+1-555-0001', '123 Admin Street, Tech City, TC 12345', 'ADMIN', NOW());

-- Insert sample users (password: user123 - BCrypt encoded)
INSERT INTO customers (first_name, last_name, email, password, phone_number, address, role, created_date) VALUES
('John', 'Doe', 'john.doe@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '+1-555-0101', '456 User Avenue, User City, UC 67890', 'USER', NOW()),
('Jane', 'Smith', 'jane.smith@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '+1-555-0102', '789 Customer Lane, Shop Town, ST 13579', 'USER', NOW()),
('Bob', 'Johnson', 'bob.johnson@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '+1-555-0103', '321 Buyer Boulevard, Purchase Park, PP 24680', 'USER', NOW());

-- Insert sample orders
INSERT INTO orders (customer_id, order_date, status, total_amount) VALUES
(2, '2024-01-15 10:30:00', 'DELIVERED', 1449.98),
(3, '2024-01-16 14:45:00', 'SHIPPED', 689.98),
(4, '2024-01-17 09:15:00', 'CONFIRMED', 329.98),
(2, '2024-01-18 16:20:00', 'PENDING', 899.99);

-- Insert sample order items
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) VALUES
-- Order 1 (John Doe)
(1, 1, 1, 1299.99, 1299.99),
(1, 4, 1, 149.99, 149.99),
-- Order 2 (Jane Smith)
(2, 3, 1, 599.99, 599.99),
(2, 7, 1, 89.99, 89.99),
-- Order 3 (Bob Johnson)
(3, 10, 2, 79.99, 159.98),
(3, 13, 1, 189.99, 189.99),
-- Order 4 (John Doe)
(4, 11, 1, 899.99, 899.99);
