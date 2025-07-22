package com.ecommerce.controller;

import com.ecommerce.dto.*;
import com.ecommerce.model.Order;
import com.ecommerce.security.UserPrincipal;
import com.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDto>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Order> orders = orderService.getAllOrders(page, size);
        Page<OrderDto> orderDtos = orders.map(orderService::convertToDto);
        return ResponseEntity.ok(orderDtos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @orderService.getOrderById(#id).orElse(null)?.customer?.id == authentication.principal.id)")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(order -> ResponseEntity.ok(orderService.convertToDto(order)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #customerId == authentication.principal.id)")
    public ResponseEntity<Page<OrderDto>> getOrdersByCustomer(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<Order> orders = orderService.getOrdersByCustomerId(customerId, page, size);
        Page<OrderDto> orderDtos = orders.map(orderService::convertToDto);
        return ResponseEntity.ok(orderDtos);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderCreateDto orderRequest, Authentication authentication) {
        try {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            // Users can only create orders for themselves
            Long customerId = userPrincipal.getId();
            
            Order order = orderService.createOrder(customerId, orderRequest.getOrderItems());
            return ResponseEntity.ok(orderService.convertToDto(order));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(null);
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest statusRequest) {
        try {
            Order order = orderService.updateOrderStatus(id, statusRequest.getStatus());
            return ResponseEntity.ok(orderService.convertToDto(order));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @orderService.getOrderById(#id).orElse(null)?.customer?.id == authentication.principal.id)")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        try {
            orderService.cancelOrder(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDto>> getOrdersByStatus(@PathVariable Order.OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        List<OrderDto> orderDtos = orders.stream()
                .map(orderService::convertToDto)
                .toList();
        return ResponseEntity.ok(orderDtos);
    }

    // Helper classes for request handling
    public static class StatusUpdateRequest {
        private Order.OrderStatus status;

        public Order.OrderStatus getStatus() {
            return status;
        }

        public void setStatus(Order.OrderStatus status) {
            this.status = status;
        }
    }

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
