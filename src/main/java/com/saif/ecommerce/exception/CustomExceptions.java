package com.saif.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * CustomExceptions - Custom exception classes for the E-commerce system
 * Contains various exception types for different scenarios
 */
public class CustomExceptions {

    /**
     * Base exception class for all e-commerce related exceptions
     */
    public static class EcommerceException extends RuntimeException {
        private final String errorCode;
        private final HttpStatus httpStatus;

        public EcommerceException(String message, String errorCode, HttpStatus httpStatus) {
            super(message);
            this.errorCode = errorCode;
            this.httpStatus = httpStatus;
        }

        public EcommerceException(String message, String errorCode, HttpStatus httpStatus, Throwable cause) {
            super(message, cause);
            this.errorCode = errorCode;
            this.httpStatus = httpStatus;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public HttpStatus getHttpStatus() {
            return httpStatus;
        }
    }

    /**
     * Exception thrown when a resource is not found
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ResourceNotFoundException extends EcommerceException {
        public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
            super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue),
                  "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND);
        }

        public ResourceNotFoundException(String message) {
            super(message, "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Exception thrown when a resource already exists
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    public static class ResourceAlreadyExistsException extends EcommerceException {
        public ResourceAlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
            super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue),
                  "RESOURCE_ALREADY_EXISTS", HttpStatus.CONFLICT);
        }

        public ResourceAlreadyExistsException(String message) {
            super(message, "RESOURCE_ALREADY_EXISTS", HttpStatus.CONFLICT);
        }
    }

    /**
     * Exception thrown when authentication fails
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class AuthenticationException extends EcommerceException {
        public AuthenticationException(String message) {
            super(message, "AUTHENTICATION_FAILED", HttpStatus.UNAUTHORIZED);
        }

        public AuthenticationException(String message, Throwable cause) {
            super(message, "AUTHENTICATION_FAILED", HttpStatus.UNAUTHORIZED, cause);
        }
    }

    /**
     * Exception thrown when authorization fails
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class AuthorizationException extends EcommerceException {
        public AuthorizationException(String message) {
            super(message, "AUTHORIZATION_FAILED", HttpStatus.FORBIDDEN);
        }

        public AuthorizationException(String message, Throwable cause) {
            super(message, "AUTHORIZATION_FAILED", HttpStatus.FORBIDDEN, cause);
        }
    }

    /**
     * Exception thrown when validation fails
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class ValidationException extends EcommerceException {
        public ValidationException(String message) {
            super(message, "VALIDATION_FAILED", HttpStatus.BAD_REQUEST);
        }

        public ValidationException(String field, String message) {
            super(String.format("Validation failed for field '%s': %s", field, message),
                  "VALIDATION_FAILED", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Exception thrown when business logic validation fails
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class BusinessLogicException extends EcommerceException {
        public BusinessLogicException(String message) {
            super(message, "BUSINESS_LOGIC_ERROR", HttpStatus.BAD_REQUEST);
        }

        public BusinessLogicException(String message, Throwable cause) {
            super(message, "BUSINESS_LOGIC_ERROR", HttpStatus.BAD_REQUEST, cause);
        }
    }

    /**
     * Exception thrown when there's insufficient stock
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InsufficientStockException extends EcommerceException {
        public InsufficientStockException(String productName, int requestedQuantity, int availableStock) {
            super(String.format("Insufficient stock for product '%s'. Requested: %d, Available: %d",
                  productName, requestedQuantity, availableStock),
                  "INSUFFICIENT_STOCK", HttpStatus.BAD_REQUEST);
        }

        public InsufficientStockException(String message) {
            super(message, "INSUFFICIENT_STOCK", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Exception thrown when an order cannot be cancelled
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class OrderNotCancellableException extends EcommerceException {
        public OrderNotCancellableException(Long orderId, String currentStatus) {
            super(String.format("Order with ID %d cannot be cancelled. Current status: %s",
                  orderId, currentStatus),
                  "ORDER_NOT_CANCELLABLE", HttpStatus.BAD_REQUEST);
        }

        public OrderNotCancellableException(String message) {
            super(message, "ORDER_NOT_CANCELLABLE", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Exception thrown when an invalid order status transition is attempted
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidOrderStatusException extends EcommerceException {
        public InvalidOrderStatusException(String currentStatus, String newStatus) {
            super(String.format("Cannot change order status from '%s' to '%s'",
                  currentStatus, newStatus),
                  "INVALID_ORDER_STATUS", HttpStatus.BAD_REQUEST);
        }

        public InvalidOrderStatusException(String message) {
            super(message, "INVALID_ORDER_STATUS", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Exception thrown when a payment fails
     */
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    public static class PaymentException extends EcommerceException {
        public PaymentException(String message) {
            super(message, "PAYMENT_FAILED", HttpStatus.PAYMENT_REQUIRED);
        }

        public PaymentException(String message, Throwable cause) {
            super(message, "PAYMENT_FAILED", HttpStatus.PAYMENT_REQUIRED, cause);
        }
    }

    /**
     * Exception thrown when JWT token is invalid
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class InvalidTokenException extends EcommerceException {
        public InvalidTokenException(String message) {
            super(message, "INVALID_TOKEN", HttpStatus.UNAUTHORIZED);
        }

        public InvalidTokenException(String message, Throwable cause) {
            super(message, "INVALID_TOKEN", HttpStatus.UNAUTHORIZED, cause);
        }
    }

    /**
     * Exception thrown when JWT token is expired
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class TokenExpiredException extends EcommerceException {
        public TokenExpiredException(String message) {
            super(message, "TOKEN_EXPIRED", HttpStatus.UNAUTHORIZED);
        }

        public TokenExpiredException(String message, Throwable cause) {
            super(message, "TOKEN_EXPIRED", HttpStatus.UNAUTHORIZED, cause);
        }
    }

    /**
     * Exception thrown when database operation fails
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static class DatabaseException extends EcommerceException {
        public DatabaseException(String message) {
            super(message, "DATABASE_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        public DatabaseException(String message, Throwable cause) {
            super(message, "DATABASE_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, cause);
        }
    }

    /**
     * Exception thrown when external service fails
     */
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public static class ExternalServiceException extends EcommerceException {
        public ExternalServiceException(String serviceName, String message) {
            super(String.format("External service '%s' failed: %s", serviceName, message),
                  "EXTERNAL_SERVICE_ERROR", HttpStatus.SERVICE_UNAVAILABLE);
        }

        public ExternalServiceException(String message, Throwable cause) {
            super(message, "EXTERNAL_SERVICE_ERROR", HttpStatus.SERVICE_UNAVAILABLE, cause);
        }
    }

    /**
     * Exception thrown when rate limit is exceeded
     */
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public static class RateLimitException extends EcommerceException {
        public RateLimitException(String message) {
            super(message, "RATE_LIMIT_EXCEEDED", HttpStatus.TOO_MANY_REQUESTS);
        }
    }
}