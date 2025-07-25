package com.saif.ecommerce.controller;

import com.saif.ecommerce.model.Order;
import com.saif.ecommerce.model.Order.OrderStatus;
import com.saif.ecommerce.model.Customer;
import com.saif.ecommerce.service.OrderService;
import com.saif.ecommerce.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * OrderController - handles all Order Management endpoints
 * Supports all required order operations with proper security
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthService authService;

    // GET /api/orders - Retrieve all orders (Admin only)
    /**
     * Get all orders with pagination (Admin only)
     * @param page page number (default: 0)
     * @param size page size (default: 10)
     * @param authHeader authorization header with JWT token
     * @return paginated list of orders
     */
    @GetMapping
    public ResponseEntity<?> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String authHeader) {

        try {
            logger.info("GET /api/orders - page: {}, size: {}", page, size);

            // Extract and validate JWT token
            String token = authService.extractTokenFromHeader(authHeader);
            if (token == null || !authService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or missing token"));
            }

            // Check admin role
            if (!authService.isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin access required"));
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Order> orders = orderService.getAllOrders(pageable);

            return ResponseEntity.ok(orders);

        } catch (Exception e) {
            logger.error("Error fetching orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch orders", "message", e.getMessage()));
        }
    }

    // GET /api/orders/{id} - Retrieve order by ID
    /**
     * Get order by ID (Owner or Admin only)
     * @param id order ID
     * @param authHeader authorization header with JWT token
     * @return order details
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        try {
            logger.info("GET /api/orders/{}", id);

            // Extract and validate JWT token
            String token = authService.extractTokenFromHeader(authHeader);
            if (token == null || !authService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or missing token"));
            }

            Order order = orderService.getOrderById(id);

            // Check if user is admin or order owner
            if (!authService.isAdmin(token)) {
                Customer customer = authService.getCustomerFromToken(token);
                if (!orderService.canCustomerAccessOrder(id, customer.getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("error", "Access denied"));
                }
            }

            return ResponseEntity.ok(order);

        } catch (RuntimeException e) {
            logger.error("Order not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Order not found", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error fetching order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch order", "message", e.getMessage()));
        }
    }

    // GET /api/orders/customer/{customerId} - Get orders by customer
    /**
     * Get orders by customer ID (Owner or Admin only)
     * @param customerId customer ID
     * @param page page number (default: 0)
     * @param size page size (default: 10)
     * @param authHeader authorization header with JWT token
     * @return paginated list of customer's orders
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getOrdersByCustomerId(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String authHeader) {

        try {
            logger.info("GET /api/orders/customer/{} - page: {}, size: {}", customerId, page, size);

            // Extract and validate JWT token
            String token = authService.extractTokenFromHeader(authHeader);
            if (token == null || !authService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or missing token"));
            }

            // Check if user is owner or admin
            if (!authService.isOwnerOrAdmin(token, customerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Order> orders = orderService.getOrdersByCustomerId(customerId, pageable);

            return ResponseEntity.ok(orders);

        } catch (RuntimeException e) {
            logger.error("Error fetching orders for customer: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to fetch orders", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error fetching orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error", "message", e.getMessage()));
        }
    }

    // GET /api/orders/status/{status} - Filter orders by status (Admin only)
    /**
     * Get orders by status (Admin only)
     * @param status order status
     * @param page page number (default: 0)
     * @param size page size (default: 10)
     * @param authHeader authorization header with JWT token
     * @return paginated list of orders with specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String authHeader) {

        try {
            logger.info("GET /api/orders/status/{} - page: {}, size: {}", status, page, size);

            // Extract and validate JWT token
            String token = authService.extractTokenFromHeader(authHeader);
            if (token == null || !authService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or missing token"));
            }

            // Check admin role
            if (!authService.isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin access required"));
            }

            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            Pageable pageable = PageRequest.of(page, size);
            Page<Order> orders = orderService.getOrdersByStatus(orderStatus, pageable);

            return ResponseEntity.ok(orders);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid order status",
                            "message", "Valid statuses: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED"));
        } catch (Exception e) {
            logger.error("Error fetching orders by status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch orders", "message", e.getMessage()));
        }
    }

    // POST /api/orders - Create new order
    /**
     * Create new order (User access)
     * @param orderRequest order creation request
     * @param authHeader authorization header with JWT token
     * @return created order
     */
    @PostMapping
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody Map<String, Object> orderRequest,
            @RequestHeader("Authorization") String authHeader) {

        try {
            logger.info("POST /api/orders - Creating new order");

            // Extract and validate JWT token
            String token = authService.extractTokenFromHeader(authHeader);
            if (token == null || !authService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or missing token"));
            }

            // Get customer from token
            Customer customer = authService.getCustomerFromToken(token);

            // Extract order items from request
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> orderItems = (List<Map<String, Object>>) orderRequest.get("orderItems");

            if (orderItems == null || orderItems.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Order must contain at least one item"));
            }

            Order createdOrder = orderService.createOrder(customer.getId(), orderItems);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);

        } catch (RuntimeException e) {
            logger.error("Error creating order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to create order", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error creating order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error", "message", e.getMessage()));
        }
    }

    // PUT /api/orders/{id}/status - Update order status (Admin only)
    /**
     * Update order status (Admin only)
     * @param id order ID
     * @param statusUpdate status update request
     * @param authHeader authorization header with JWT token
     * @return updated order
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate,
            @RequestHeader("Authorization") String authHeader) {

        try {
            logger.info("PUT /api/orders/{}/status", id);

            // Extract and validate JWT token
            String token = authService.extractTokenFromHeader(authHeader);
            if (token == null || !authService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or missing token"));
            }

            // Check admin role
            if (!authService.isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin access required"));
            }

            String statusStr = statusUpdate.get("status");
            if (statusStr == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "status is required"));
            }

            OrderStatus newStatus = OrderStatus.valueOf(statusStr.toUpperCase());
            Order updatedOrder = orderService.updateOrderStatus(id, newStatus);

            return ResponseEntity.ok(updatedOrder);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid order status",
                            "message", "Valid statuses: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED"));
        } catch (RuntimeException e) {
            logger.error("Error updating order status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to update order status", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error updating order status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error", "message", e.getMessage()));
        }
    }

    // DELETE /api/orders/{id} - Cancel order
    /**
     * Cancel order (Owner or Admin only)
     * @param id order ID
     * @param authHeader authorization header with JWT token
     * @return success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelOrder(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        try {
            logger.info("DELETE /api/orders/{} - Cancelling order", id);

            // Extract and validate JWT token
            String token = authService.extractTokenFromHeader(authHeader);
            if (token == null || !authService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or missing token"));
            }

            // Check if user is admin or order owner
            if (!authService.isAdmin(token)) {
                Customer customer = authService.getCustomerFromToken(token);
                if (!orderService.canCustomerAccessOrder(id, customer.getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("error", "Access denied"));
                }
            }

            orderService.cancelOrder(id);
            return ResponseEntity.ok(Map.of("message", "Order cancelled successfully"));

        } catch (RuntimeException e) {
            logger.error("Error cancelling order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to cancel order", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error cancelling order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error", "message", e.getMessage()));
        }
    }

    // Additional endpoints for better functionality

    /**
     * Get order statistics (Admin only)
     * @param authHeader authorization header with JWT token
     * @return order statistics by status
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getOrderStatistics(@RequestHeader("Authorization") String authHeader) {
        try {
            logger.info("GET /api/orders/statistics");

            // Extract and validate JWT token
            String token = authService.extractTokenFromHeader(authHeader);
            if (token == null || !authService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or missing token"));
            }

            // Check admin role
            if (!authService.isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin access required"));
            }

            Map<OrderStatus, Long> statistics = orderService.getOrderStatistics();
            return ResponseEntity.ok(statistics);

        } catch (Exception e) {
            logger.error("Error fetching order statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch statistics", "message", e.getMessage()));
        }
    }

    /**
     * Get total revenue (Admin only)
     * @param authHeader authorization header with JWT token
     * @return total revenue
     */
    @GetMapping("/revenue")
    public ResponseEntity<?> getOrderRevenue(@RequestHeader("Authorization") String authHeader) {
        try {
            logger.info("GET /api/orders/revenue");

            // Extract and validate JWT token
            String token = authService.extractTokenFromHeader(authHeader);
            if (token == null || !authService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or missing token"));
            }

            // Check admin role
            if (!authService.isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin access required"));
            }

            BigDecimal totalRevenue = orderService.getTotalRevenue();
            return ResponseEntity.ok(Map.of("totalRevenue", totalRevenue));

        } catch (Exception e) {
            logger.error("Error fetching total revenue: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch revenue", "message", e.getMessage()));
        }
    }

    /**
     * Get customer's cancellable orders (User access)
     * @param authHeader authorization header with JWT token
     * @return list of cancellable orders
     */
    @GetMapping("/my-orders/cancellable")
    public ResponseEntity<?> getMyCancellableOrders(@RequestHeader("Authorization") String authHeader) {
        try {
            logger.info("GET /api/orders/my-orders/cancellable");

            // Extract and validate JWT token
            String token = authService.extractTokenFromHeader(authHeader);
            if (token == null || !authService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or missing token"));
            }

            Customer customer = authService.getCustomerFromToken(token);
            List<Order> cancellableOrders = orderService.getCustomerCancellableOrders(customer.getId());

            return ResponseEntity.ok(cancellableOrders);

        } catch (Exception e) {
            logger.error("Error fetching cancellable orders: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch cancellable orders", "message", e.getMessage()));
        }
    }

    /**
     * Get orders by date range (Admin only)
     * @param startDate start date (ISO format)
     * @param endDate end date (ISO format)
     * @param page page number (default: 0)
     * @param size page size (default: 10)
     * @param authHeader authorization header with JWT token
     * @return paginated list of orders in date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<?> getOrdersByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String authHeader) {

        try {
            logger.info("GET /api/orders/date-range - start: {}, end: {}", startDate, endDate);

            // Extract and validate JWT token
            String token = authService.extractTokenFromHeader(authHeader);
            if (token == null || !authService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or missing token"));
            }

            // Check admin role
            if (!authService.isAdmin(token)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin access required"));
            }

            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            Pageable pageable = PageRequest.of(page, size);

            Page<Order> orders = orderService.getOrdersByDateRange(start, end, pageable);
            return ResponseEntity.ok(orders);

        } catch (Exception e) {
            logger.error("Error fetching orders by date range: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to fetch orders", "message", e.getMessage()));
        }
    }
}