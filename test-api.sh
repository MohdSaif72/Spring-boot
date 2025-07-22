#!/bin/bash

BASE_URL="http://localhost:8080"

echo "=== E-Commerce API Test Script ==="
echo "Base URL: $BASE_URL"
echo ""

# Function to make HTTP requests with better formatting
make_request() {
    local method=$1
    local endpoint=$2
    local data=$3
    local token=$4
    
    echo "[$method] $endpoint"
    
    if [ -n "$token" ]; then
        if [ -n "$data" ]; then
            curl -s -X $method \
                -H "Content-Type: application/json" \
                -H "Authorization: Bearer $token" \
                -d "$data" \
                "$BASE_URL$endpoint" | jq '.' 2>/dev/null || curl -s -X $method -H "Authorization: Bearer $token" "$BASE_URL$endpoint"
        else
            curl -s -X $method \
                -H "Authorization: Bearer $token" \
                "$BASE_URL$endpoint" | jq '.' 2>/dev/null || curl -s -X $method -H "Authorization: Bearer $token" "$BASE_URL$endpoint"
        fi
    else
        if [ -n "$data" ]; then
            curl -s -X $method \
                -H "Content-Type: application/json" \
                -d "$data" \
                "$BASE_URL$endpoint" | jq '.' 2>/dev/null || curl -s -X $method -H "Content-Type: application/json" -d "$data" "$BASE_URL$endpoint"
        else
            curl -s -X $method "$BASE_URL$endpoint" | jq '.' 2>/dev/null || curl -s -X $method "$BASE_URL$endpoint"
        fi
    fi
    echo ""
    echo "---"
    echo ""
}

# Wait for application to start
echo "Waiting for application to start..."
sleep 5

echo "=== 1. AUTHENTICATION TESTS ==="

# Admin Login
echo "Logging in as Admin..."
ADMIN_LOGIN_RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{"email":"admin@ecommerce.com","password":"admin123"}' \
    "$BASE_URL/api/auth/login")

ADMIN_TOKEN=$(echo $ADMIN_LOGIN_RESPONSE | jq -r '.token // empty' 2>/dev/null)
echo "Admin Token: ${ADMIN_TOKEN:0:50}..."
echo ""

# User Login
echo "Logging in as User..."
USER_LOGIN_RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{"email":"john.doe@email.com","password":"user123"}' \
    "$BASE_URL/api/auth/login")

USER_TOKEN=$(echo $USER_LOGIN_RESPONSE | jq -r '.token // empty' 2>/dev/null)
echo "User Token: ${USER_TOKEN:0:50}..."
echo ""

echo "=== 2. PRODUCT MANAGEMENT TESTS ==="

# Test all product endpoints
make_request "GET" "/api/products" "" ""
make_request "GET" "/api/products?page=0&size=5" "" ""
make_request "GET" "/api/products/1" "" ""
make_request "GET" "/api/products/category/Electronics" "" ""
make_request "GET" "/api/products/search?searchTerm=laptop" "" ""

# Admin-only product operations
if [ -n "$ADMIN_TOKEN" ]; then
    echo "Testing admin product operations..."
    
    # Create new product
    NEW_PRODUCT='{"name":"Test Product","description":"A test product","price":99.99,"category":"Test","stockQuantity":10}'
    make_request "POST" "/api/products" "$NEW_PRODUCT" "$ADMIN_TOKEN"
    
    # Update product
    UPDATE_PRODUCT='{"name":"Updated Test Product","description":"An updated test product","price":119.99,"category":"Test","stockQuantity":15}'
    make_request "PUT" "/api/products/1" "$UPDATE_PRODUCT" "$ADMIN_TOKEN"
fi

echo "=== 3. CUSTOMER MANAGEMENT TESTS ==="

# Public registration
NEW_CUSTOMER='{"firstName":"Test","lastName":"User","email":"test@example.com","password":"password123","phoneNumber":"555-0123","address":"123 Test St"}'
make_request "POST" "/api/customers/register" "$NEW_CUSTOMER" ""

# Admin customer operations
if [ -n "$ADMIN_TOKEN" ]; then
    make_request "GET" "/api/customers" "" "$ADMIN_TOKEN"
    make_request "GET" "/api/customers/1" "" "$ADMIN_TOKEN"
fi

# User profile operations
if [ -n "$USER_TOKEN" ]; then
    make_request "GET" "/api/customers/profile" "" "$USER_TOKEN"
    make_request "GET" "/api/customers/2/orders" "" "$USER_TOKEN"
fi

echo "=== 4. ORDER MANAGEMENT TESTS ==="

# Admin order operations
if [ -n "$ADMIN_TOKEN" ]; then
    make_request "GET" "/api/orders" "" "$ADMIN_TOKEN"
    make_request "GET" "/api/orders/1" "" "$ADMIN_TOKEN"
    make_request "GET" "/api/orders/customer/2" "" "$ADMIN_TOKEN"
    make_request "GET" "/api/orders/status/PENDING" "" "$ADMIN_TOKEN"
    
    # Update order status
    make_request "PUT" "/api/orders/1/status" '{"status":"SHIPPED"}' "$ADMIN_TOKEN"
fi

# User order operations
if [ -n "$USER_TOKEN" ]; then
    # Create new order
    NEW_ORDER='{"orderItems":[{"productId":1,"quantity":1},{"productId":2,"quantity":2}]}'
    make_request "POST" "/api/orders" "$NEW_ORDER" "$USER_TOKEN"
    
    make_request "GET" "/api/orders/customer/2" "" "$USER_TOKEN"
fi

echo "=== API Test Complete ==="
