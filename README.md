# Spring Boot E-Commerce API - Capstone Project

A comprehensive E-Commerce Management System built with Spring Boot, featuring JWT authentication, role-based access control, and complete CRUD operations for products, customers, and orders.

## 🚀 Features

- **JWT Authentication & Authorization**
- **Role-based Access Control (USER, ADMIN)**
- **Product Management** with search and pagination
- **Customer Management** with profile operations
- **Order Management** with status tracking
- **MySQL Database** with complete schema
- **RESTful API Design**
- **Input Validation & Error Handling**
- **Docker Support**

## 📋 Prerequisites

- Java 17+
- Maven 3.6+
- Docker & Docker Compose
- MySQL 8.0+ (or use Docker)

## 🛠️ Quick Setup

### 1. Clone and Build

```bash
git clone <repository-url>
cd ecommerce-api
mvn clean package -DskipTests
```

### 2. Start Database

```bash
# Using the provided script
./setup-database.sh

# Or manually with Docker
docker run --name ecommerce-mysql \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_DATABASE=ecommerce_db \
  -p 3306:3306 \
  -d mysql:8.0
```

### 3. Run Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 🔑 Test Credentials

### Admin User
- **Email:** `admin@ecommerce.com`
- **Password:** `admin123`

### Regular User
- **Email:** `john.doe@email.com`
- **Password:** `user123`

## 📚 API Documentation

### Authentication Endpoints

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@ecommerce.com",
  "password": "admin123"
}
```

#### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "password123",
  "phoneNumber": "555-0123",
  "address": "123 Main St"
}
```

### Product Management

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/products` | Public | Get all products (paginated) |
| GET | `/api/products/{id}` | Public | Get product by ID |
| GET | `/api/products/category/{category}` | Public | Get products by category |
| GET | `/api/products/search?searchTerm=term` | Public | Search products |
| POST | `/api/products` | Admin | Create new product |
| PUT | `/api/products/{id}` | Admin | Update product |
| DELETE | `/api/products/{id}` | Admin | Delete product |

#### Example: Create Product
```http
POST /api/products
Authorization: Bearer {admin-token}
Content-Type: application/json

{
  "name": "Gaming Laptop",
  "description": "High-performance gaming laptop",
  "price": 1299.99,
  "category": "Electronics",
  "stockQuantity": 10
}
```

### Customer Management

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/customers` | Admin | Get all customers |
| GET | `/api/customers/{id}` | Admin/Owner | Get customer by ID |
| GET | `/api/customers/profile` | User | Get current user profile |
| POST | `/api/customers/register` | Public | Register new customer |
| PUT | `/api/customers/{id}` | Admin/Owner | Update customer |
| DELETE | `/api/customers/{id}` | Admin | Delete customer |
| GET | `/api/customers/{id}/orders` | Admin/Owner | Get customer orders |

### Order Management

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/orders` | Admin | Get all orders |
| GET | `/api/orders/{id}` | Admin/Owner | Get order by ID |
| GET | `/api/orders/customer/{customerId}` | Admin/Owner | Get customer orders |
| POST | `/api/orders` | User | Create new order |
| PUT | `/api/orders/{id}/status` | Admin | Update order status |
| DELETE | `/api/orders/{id}` | Admin/Owner | Cancel order |
| GET | `/api/orders/status/{status}` | Admin | Filter orders by status |

#### Example: Create Order
```http
POST /api/orders
Authorization: Bearer {user-token}
Content-Type: application/json

{
  "orderItems": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 3,
      "quantity": 1
    }
  ]
}
```

## 🗄️ Database Schema

### Products Table
```sql
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(255) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    created_date DATETIME NOT NULL,
    updated_date DATETIME NOT NULL
);
```

### Customers Table
```sql
CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    address TEXT,
    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    created_date DATETIME NOT NULL
);
```

### Orders Table
```sql
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    order_date DATETIME NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED') NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);
```

### Order Items Table
```sql
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
```

## 🧪 Testing

### Run API Tests
```bash
# Start the application first, then run:
./test-api.sh
```

### Manual Testing with cURL

#### Get all products
```bash
curl -X GET http://localhost:8080/api/products
```

#### Login and get token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@ecommerce.com","password":"admin123"}'
```

#### Create product (Admin only)
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"name":"Test Product","description":"Test","price":99.99,"category":"Test","stockQuantity":5}'
```

## 🐳 Docker Deployment

### Using Docker Compose
```bash
docker-compose up -d
```

### Build and Run Manually
```bash
# Build the application
mvn clean package -DskipTests

# Build Docker image
docker build -t ecommerce-api .

# Run with database
docker run --name ecommerce-app \
  --link ecommerce-mysql:mysql \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/ecommerce_db \
  ecommerce-api
```

## 📁 Project Structure

```
src/main/java/com/ecommerce/
├── EcommerceApplication.java
├── config/
│   ├── SecurityConfig.java
│   └── CorsConfig.java
├── controller/
│   ├── AuthController.java
│   ├── ProductController.java
│   ├── CustomerController.java
│   └── OrderController.java
├── service/
│   ├── AuthService.java
│   ├── ProductService.java
│   ├── CustomerService.java
│   └── OrderService.java
├── repository/
│   ├── ProductRepository.java
│   ├── CustomerRepository.java
│   ├── OrderRepository.java
│   └── OrderItemRepository.java
├── model/
│   ├── Product.java
│   ├── Customer.java
│   ├── Order.java
│   └── OrderItem.java
├── dto/
│   ├── ProductDto.java
│   ├── CustomerDto.java
│   ├── OrderDto.java
│   └── AuthDto.java
├── security/
│   ├── JwtUtils.java
│   ├── UserPrincipal.java
│   ├── AuthTokenFilter.java
│   └── CustomUserDetailsService.java
└── exception/
    ├── GlobalExceptionHandler.java
    └── CustomExceptions.java
```

## 🔐 Security Features

- **JWT Token Authentication**
- **BCrypt Password Encryption**
- **Role-based Authorization**
- **CORS Configuration**
- **Input Validation**
- **Global Exception Handling**

## 📈 Sample Data

The database is initialized with:
- **15 Products** across various categories
- **1 Admin User** and **3 Regular Users**
- **4 Sample Orders** with order items

## 🚀 Advanced Features

- **Pagination** for all list endpoints
- **Search Functionality** for products
- **Stock Management** with automatic updates
- **Order Status Tracking**
- **Audit Fields** (created_date, updated_date)
- **Database Indexing** for performance

## 📞 Support

For questions or issues, please refer to the code comments or create an issue in the repository.

---

**Note:** This is a capstone project demonstrating Spring Boot best practices and enterprise-level API development.
