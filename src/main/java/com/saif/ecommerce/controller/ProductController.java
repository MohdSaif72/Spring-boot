package com.saif.ecommerce.controller;

import com.saif.ecommerce.dto.ProductDto;
import com.saif.ecommerce.model.Product;
import com.saif.ecommerce.service.ProductService;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Product management.
 * Provides endpoints for CRUD operations on products with proper security controls.
 * 
 * @author Saif
 * @version 1.0
 * @since 1.0
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    /**
     * Retrieves all products with pagination and sorting.
     * 
     * @param page the page number (default: 0)
     * @param size the page size (default: 10)
     * @param sortBy the field to sort by (default: id)
     * @param sortDir the sort direction (default: asc)
     * @return ResponseEntity containing a page of ProductDto objects
     */
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        logger.info("GET /api/products - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        
        Page<Product> products = productService.getAllProducts(page, size, sortBy, sortDir);
        Page<ProductDto> productDtos = products.map(productService::convertToDto);
        
        logger.debug("Returning {} products", productDtos.getNumberOfElements());
        return ResponseEntity.ok(productDtos);
    }

    /**
     * Get product by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        logger.info("GET /api/products/{}", id);
        return productService.getProductById(id)
                .map(product -> ResponseEntity.ok(productService.convertToDto(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get products by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<ProductDto>> getProductsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.info("GET /api/products/category/{} - page: {}, size: {}", category, page, size);
        Page<Product> products = productService.getProductsByCategory(category, page, size);
        Page<ProductDto> productDtos = products.map(productService::convertToDto);
        return ResponseEntity.ok(productDtos);
    }

    /**
     * Search products by name or description
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ProductDto>> searchProducts(
            @RequestParam String searchTerm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.info("GET /api/products/search?searchTerm={}", searchTerm);
        Page<Product> products = productService.searchProducts(searchTerm, page, size);
        Page<ProductDto> productDtos = products.map(productService::convertToDto);
        return ResponseEntity.ok(productDtos);
    }

    /**
     * Create new product (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        logger.info("POST /api/products - Creating product: {}", productDto.getName());
        Product product = productService.createProduct(productDto);
        return ResponseEntity.ok(productService.convertToDto(product));
    }

    /**
     * Update product (Admin only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto productDto) {
        logger.info("PUT /api/products/{} - Updating product", id);
        try {
            Product product = productService.updateProduct(id, productDto);
            return ResponseEntity.ok(productService.convertToDto(product));
        } catch (RuntimeException e) {
            logger.error("Product not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete product (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        logger.info("DELETE /api/products/{}", id);
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            logger.error("Product not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all product categories (Public)
     */
    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        logger.info("GET /api/products/categories");
        return ResponseEntity.ok(productService.getAllCategories());
    }

    /**
     * Get low stock products (Admin only)
     */
    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getLowStockProducts(@RequestParam(defaultValue = "10") int threshold) {
        logger.info("GET /api/products/low-stock?threshold={}", threshold);
        return ResponseEntity.ok(productService.getLowStockProducts(threshold));
    }
}