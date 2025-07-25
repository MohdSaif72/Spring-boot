package com.saif.ecommerce.dto;

import javax.validation.constraints.*;

/**
 * AuthDto - Data Transfer Object for Authentication operations
 * Contains nested DTOs for login, register, and response operations
 */
public class AuthDto {

    /**
     * Login Request DTO
     */
    public static class LoginRequest {
        
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String email;
        
        @NotBlank(message = "Password is required")
        private String password;

        // Default constructor
        public LoginRequest() {}

        // Constructor
        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        // Getters and Setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public String toString() {
            return "LoginRequest{" +
                    "email='" + email + '\'' +
                    '}';
        }
    }

    /**
     * Registration Request DTO
     */
    public static class RegisterRequest {
        
        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must not exceed 50 characters")
        private String firstName;
        
        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must not exceed 50 characters")
        private String lastName;
        
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        private String email;
        
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        private String password;
        
        @Size(max = 20, message = "Phone number must not exceed 20 characters")
        private String phoneNumber;
        
        @Size(max = 200, message = "Address must not exceed 200 characters")
        private String address;

        // Default constructor
        public RegisterRequest() {}

        // Constructor
        public RegisterRequest(String firstName, String lastName, String email, String password) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.password = password;
        }

        // Getters and Setters
        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        @Override
        public String toString() {
            return "RegisterRequest{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", email='" + email + '\'' +
                    ", phoneNumber='" + phoneNumber + '\'' +
                    ", address='" + address + '\'' +
                    '}';
        }
    }

    /**
     * Authentication Response DTO
     */
    public static class AuthResponse {
        
        private String token;
        private String type = "Bearer";
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private String role;
        private Long expiresIn;
        private String message;

        // Default constructor
        public AuthResponse() {}

        // Constructor
        public AuthResponse(String token, Long id, String email, String firstName, 
                           String lastName, String role, Long expiresIn) {
            this.token = token;
            this.id = id;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
            this.expiresIn = expiresIn;
        }

        // Getters and Setters
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public Long getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "AuthResponse{" +
                    "type='" + type + '\'' +
                    ", id=" + id +
                    ", email='" + email + '\'' +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", role='" + role + '\'' +
                    ", expiresIn=" + expiresIn +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    /**
     * Password Change Request DTO
     */
    public static class PasswordChangeRequest {
        
        @NotBlank(message = "Current password is required")
        private String oldPassword;
        
        @NotBlank(message = "New password is required")
        @Size(min = 6, message = "New password must be at least 6 characters long")
        private String newPassword;

        // Default constructor
        public PasswordChangeRequest() {}

        // Constructor
        public PasswordChangeRequest(String oldPassword, String newPassword) {
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
        }

        // Getters and Setters
        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        @Override
        public String toString() {
            return "PasswordChangeRequest{" +
                    "oldPassword='[PROTECTED]'" +
                    ", newPassword='[PROTECTED]'" +
                    '}';
        }
    }
}