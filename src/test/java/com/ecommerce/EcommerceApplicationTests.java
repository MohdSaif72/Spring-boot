package com.ecommerce;

import com.ecommerce.controller.ProductController;
import com.ecommerce.model.Customer;
import com.ecommerce.model.Product;
import com.ecommerce.repository.CustomerRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the E-Commerce Application.
 * Tests the complete application stack including controllers, services, and repositories.
 * 
 * @author E-Commerce Team
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@Transactional
class EcommerceApplicationTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Create test admin user
        Customer admin = new Customer();
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Customer.Role.ADMIN);
        admin.setCreatedDate(LocalDateTime.now());
        customerRepository.save(admin);

        // Create test regular user
        Customer user = new Customer();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("user@test.com");
        user.setPassword(passwordEncoder.encode("user123"));
        user.setRole(Customer.Role.USER);
        user.setCreatedDate(LocalDateTime.now());
        customerRepository.save(user);

        // Generate tokens
        adminToken = jwtUtils.generateTokenFromEmail("admin@test.com");
        userToken = jwtUtils.generateTokenFromEmail("user@test.com");

        // Create test product
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setCategory("Electronics");
        product.setStockQuantity(10);
        product.setCreatedDate(LocalDateTime.now());
        product.setUpdatedDate(LocalDateTime.now());
        productRepository.save(product);
    }

    @Test
    void contextLoads() {
        // Basic context loading test
    }

    @Test
    void testGetAllProducts_Public_Success() throws Exception {
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.totalElements").exists());
    }

    @Test
    void testGetProductById_Public_Success() throws Exception {
        Product product = productRepository.findAll().get(0);
        
        mockMvc.perform(get("/api/products/{id}", product.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void testCreateProduct_Admin_Success() throws Exception {
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setDescription("New Description");
        newProduct.setPrice(new BigDecimal("149.99"));
        newProduct.setCategory("Books");
        newProduct.setStockQuantity(5);

        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Product"));
    }

    @Test
    void testCreateProduct_User_Forbidden() throws Exception {
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setDescription("New Description");
        newProduct.setPrice(new BigDecimal("149.99"));
        newProduct.setCategory("Books");
        newProduct.setStockQuantity(5);

        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateProduct_Admin_Success() throws Exception {
        Product product = productRepository.findAll().get(0);
        product.setName("Updated Product");
        product.setPrice(new BigDecimal("199.99"));

        mockMvc.perform(put("/api/products/{id}", product.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"));
    }

    @Test
    void testDeleteProduct_Admin_Success() throws Exception {
        Product product = productRepository.findAll().get(0);

        mockMvc.perform(delete("/api/products/{id}", product.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}
