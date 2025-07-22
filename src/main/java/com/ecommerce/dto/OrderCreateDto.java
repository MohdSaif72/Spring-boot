package com.ecommerce.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public class OrderCreateDto {
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotEmpty(message = "Order items are required")
    private List<OrderItemCreateDto> orderItems;
    
    public OrderCreateDto() {}
    
    // Getters and Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    
    public List<OrderItemCreateDto> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemCreateDto> orderItems) { this.orderItems = orderItems; }
}
