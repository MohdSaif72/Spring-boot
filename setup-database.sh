#!/bin/bash

echo "Setting up E-Commerce Database..."

# Start MySQL using Docker
echo "Starting MySQL container..."
docker run --name ecommerce-mysql \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_DATABASE=ecommerce_db \
  -e MYSQL_USER=ecommerce_user \
  -e MYSQL_PASSWORD=ecommerce_pass \
  -p 3306:3306 \
  -d mysql:8.0

# Wait for MySQL to be ready
echo "Waiting for MySQL to be ready..."
sleep 30

# Execute the database initialization script
echo "Initializing database with sample data..."
docker exec -i ecommerce-mysql mysql -uroot -ppassword < database/init.sql

echo "Database setup complete!"
echo "MySQL is running on localhost:3306"
echo "Database: ecommerce_db"
echo "Username: root"
echo "Password: password"
echo ""
echo "Test credentials:"
echo "Admin - Email: admin@ecommerce.com, Password: admin123"
echo "User  - Email: john.doe@email.com, Password: user123"
