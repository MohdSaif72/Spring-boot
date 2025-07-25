package com.saif.ecommerce.controller;

import com.saif.ecommerce.dto.CustomerDto;
import com.saif.ecommerce.model.Customer;
import com.saif.ecommerce.model.Order;
import com.saif.ecommerce.service.CustomerService;
import com.saif.ecommerce.service.AuthService;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * REST Controller for Customer management.
 * Provides endpoints for customer CRUD operations with proper security controls.
 * 
 * @author Saif
 * @version 1.0
 * @since 1.0
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AuthService authService;

    /**
     * Get all customers (Admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CustomerDto>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.info("GET /api/customers - page: {}, size: {}", page, size);
        Page<Customer> customers = customerService.getAllCustomers(page, size);
        Page<CustomerDto> customerDtos = customers.map(customerService::convertToDto);
        return ResponseEntity.ok(customerDtos);
    }

    /**
     * Get customer by ID (Admin or own profile)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Long id, HttpServletRequest request) {
        logger.info("GET /api/customers/{}", id);
        
        // Check if user is accessing their own profile
        String token = authService.extractTokenFromHeader(request.getHeader("Authorization"));
        String email = authService.getEmailFromToken(token);
        String role = authService.getRoleFromToken(token);
        
        if (!"ADMIN".equals(role)) {
            Customer requestingCustomer = customerService.getCustomerByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            if (!requestingCustomer.getId().equals(id)) {
                return ResponseEntity.status(403).build(); // Forbidden
            }
        }
        
        return customerService.getCustomerById(id)
                .map(customer -> ResponseEntity.ok(customerService.convertToDto(customer)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get current user's profile
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CustomerDto> getCurrentUserProfile(HttpServletRequest request) {
        logger.info("GET /api/customers/profile");
        
        String token = authService.extractTokenFromHeader(request.getHeader("Authorization"));
        String email = authService.getEmailFromToken(token);
        
        return customerService.getCustomerByEmail(email)
                .map(customer -> ResponseEntity.ok(customerService.convertToDto(customer)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Register new customer (Public)
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody CustomerDto customerDto) {
        logger.info("POST /api/customers/register - New customer: {}", customerDto.getEmail());
        
        try {
            Customer customer = customerService.registerCustomer(customerDto);
            return ResponseEntity.ok(customerService.convertToDto(customer));
        } catch (RuntimeException e) {
            logger.error("Customer registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Registration failed: " + e.getMessage()));
        }
    }

    /**
     * Update customer (Admin or own profile)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Long id, 
                                                     @Valid @RequestBody CustomerDto customerDto,
                                                     HttpServletRequest request) {
        logger.info("PUT /api/customers/{}", id);
        
        // Check if user is updating their own profile
        String token = authService.extractTokenFromHeader(request.getHeader("Authorization"));
        String email = authService.getEmailFromToken(token);
        String role = authService.getRoleFromToken(token);
        
        if (!"ADMIN".equals(role)) {
            Customer requestingCustomer = customerService.getCustomerByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            if (!requestingCustomer.getId().equals(id)) {
                return ResponseEntity.status(403).build(); // Forbidden
            }
        }
        
        try {
            Customer customer = customerService.updateCustomer(id, customerDto);
            return ResponseEntity.ok(customerService.convertToDto(customer));
        } catch (RuntimeException e) {
            logger.error("Customer update failed for id {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete customer (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        logger.info("DELETE /api/customers/{}", id);
        
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.ok(new MessageResponse("Customer deleted successfully"));
        } catch (RuntimeException e) {
            logger.error("Customer deletion failed for id {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get customer's order history (Admin or own orders)
     */
    @GetMapping("/{id}/orders")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<Order>> getCustomerOrders(@PathVariable Long id, HttpServletRequest request) {
        logger.info("GET /api/customers/{}/orders", id);
        
        // Check if user is accessing their own orders
        String token = authService.extractTokenFromHeader(request.getHeader("Authorization"));
        String email = authService.getEmailFromToken(token);
        String role = authService.getRoleFromToken(token);
        
        if (!"ADMIN".equals(role)) {
            Customer requestingCustomer = customerService.getCustomerByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            if (!requestingCustomer.getId().equals(id)) {
                return ResponseEntity.status(403).build(); // Forbidden
            }
        }
        
        try {
            List<Order> orders = customerService.getCustomerOrders(id);
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            logger.error("Failed to get orders for customer {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Search customers by name (Admin only)
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> searchCustomers(@RequestParam String searchTerm) {
        logger.info("GET /api/customers/search?searchTerm={}", searchTerm);
        
        try {
            List<Customer> customers = customerService.searchCustomersByName(searchTerm);
            List<CustomerDto> customerDtos = customers.stream()
                    .map(customerService::convertToDto)
                    .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(customerDtos);
        } catch (Exception e) {
            logger.error("Customer search failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Search failed: " + e.getMessage()));
        }
    }

    /**
     * Update customer role (Admin only)
     */
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCustomerRole(@PathVariable Long id, @RequestParam String role) {
        logger.info("PATCH /api/customers/{}/role - New role: {}", id, role);
        
        try {
            customerService.updateCustomerRole(id, Customer.Role.valueOf(role.toUpperCase()));
            return ResponseEntity.ok(new MessageResponse("Customer role updated successfully"));
        } catch (RuntimeException e) {
            logger.error("Role update failed for customer {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Role update failed: " + e.getMessage()));
        }
    }

    /**
     * Helper class for response messages
     */
    public static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}