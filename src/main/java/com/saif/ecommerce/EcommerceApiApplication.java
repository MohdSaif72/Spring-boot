package com.saif.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application Class
 * Entry point for the E-Commerce API
 * 
 * @author Saif
 * @version 1.0
 */
@SpringBootApplication
public class EcommerceApiApplication {
    
    /**
     * Main method to start the Spring Boot application
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(EcommerceApiApplication.class, args);
    }
}