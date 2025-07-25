package com.saif.ecommerce.controller;

import com.saif.ecommerce.dto.AuthDto;
import com.saif.ecommerce.service.AuthService;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Authentication operations.
 * Handles user login, registration, and token management.
 * 
 * @author Saif
 * @version 1.0
 * @since 1.0
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * Authenticate user and return JWT token
     * 
     * @param loginRequest contains email and password
     * @return ResponseEntity with authentication response
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthDto.LoginRequest loginRequest) {
        logger.info("POST /api/auth/login - User login attempt: {}", loginRequest.getEmail());
        
        try {
            AuthDto.AuthResponse authResponse = authService.login(loginRequest);
            logger.info("User logged in successfully: {}", loginRequest.getEmail());
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            logger.error("Login failed for user: {} - {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Authentication failed: " + e.getMessage()));
        }
    }

    /**
     * Register new user
     * 
     * @param registerRequest contains user registration details
     * @return ResponseEntity with registration response
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody AuthDto.RegisterRequest registerRequest) {
        logger.info("POST /api/auth/register - New user registration: {}", registerRequest.getEmail());
        
        try {
            AuthDto.AuthResponse authResponse = authService.register(registerRequest);
            logger.info("User registered successfully: {}", registerRequest.getEmail());
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            logger.error("Registration failed for user: {} - {}", registerRequest.getEmail(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Registration failed: " + e.getMessage()));
        }
    }

    /**
     * Refresh JWT token
     * 
     * @param request HTTP request containing Authorization header
     * @return ResponseEntity with new token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(javax.servlet.http.HttpServletRequest request) {
        logger.info("POST /api/auth/refresh - Token refresh request");
        
        try {
            String token = authService.extractTokenFromHeader(request.getHeader("Authorization"));
            if (token != null && authService.validateToken(token)) {
                String newToken = authService.refreshToken(token);
                
                AuthDto.AuthResponse response = new AuthDto.AuthResponse();
                response.setToken(newToken);
                response.setMessage("Token refreshed successfully");
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Invalid token"));
            }
        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Token refresh failed: " + e.getMessage()));
        }
    }

    /**
     * Change password for authenticated user
     * 
     * @param passwordChangeRequest contains old and new password
     * @param request HTTP request for getting current user
     * @return ResponseEntity with success message
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody AuthDto.PasswordChangeRequest passwordChangeRequest,
            javax.servlet.http.HttpServletRequest request) {
        
        logger.info("POST /api/auth/change-password - Password change request");
        
        try {
            String token = authService.extractTokenFromHeader(request.getHeader("Authorization"));
            String email = authService.getEmailFromToken(token);
            
            authService.changePassword(email, passwordChangeRequest);
            logger.info("Password changed successfully for user: {}", email);
            
            return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
        } catch (Exception e) {
            logger.error("Password change failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Password change failed: " + e.getMessage()));
        }
    }

    /**
     * Logout user (invalidate token)
     * 
     * @param request HTTP request containing Authorization header
     * @return ResponseEntity with success message
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(javax.servlet.http.HttpServletRequest request) {
        logger.info("POST /api/auth/logout - User logout request");
        
        try {
            String token = authService.extractTokenFromHeader(request.getHeader("Authorization"));
            authService.logout(token);
            
            return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
        } catch (Exception e) {
            logger.error("Logout failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Logout failed: " + e.getMessage()));
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