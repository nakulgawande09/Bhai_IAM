package com.auth.oidcprovider.controllers;

import com.auth.oidcprovider.dto.ApiResponse;
import com.auth.oidcprovider.exceptions.ResourceNotFoundException;
import com.auth.oidcprovider.exceptions.UnauthorizedException;
import com.auth.oidcprovider.models.Product;
import com.auth.oidcprovider.models.User;
import com.auth.oidcprovider.services.ProductService;
import com.auth.oidcprovider.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for product operations.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    private final ProductService productService;
    private final SecurityUtils securityUtils;
    
    public ProductController(ProductService productService, SecurityUtils securityUtils) {
        this.productService = productService;
        this.securityUtils = securityUtils;
    }
    
    /**
     * Gets all products accessible by the authenticated user.
     *
     * @return list of accessible products
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAccessibleProducts() {
        Collection<String> roles = securityUtils.getCurrentUserRoles();
        List<Product> products;
        
        if (roles.contains("ADMIN")) {
            products = productService.getAllProducts();
        } else {
            products = roles.stream()
                    .flatMap(role -> productService.getProductsByRole(role).stream())
                    .distinct()
                    .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", products));
    }
    
    /**
     * Gets all products (requires authentication).
     *
     * @return list of all products
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success("All products retrieved successfully", products));
    }
    
    /**
     * Gets a specific iPhone product (accessible by CRM Rep, Sales Manager, and Admin).
     *
     * @return the iPhone product
     */
    @GetMapping("/iphone")
    public ResponseEntity<ApiResponse<Product>> getIphone() {
        Product product = productService.getProductByName("iPhone")
                .orElseThrow(() -> new ResourceNotFoundException("Product", "name", "iPhone"));
        
        validateAccess(product);
        return ResponseEntity.ok(ApiResponse.success("iPhone retrieved successfully", product));
    }
    
    /**
     * Gets a specific iPad product (accessible by Sales Manager and Admin).
     *
     * @return the iPad product
     */
    @GetMapping("/ipad")
    public ResponseEntity<ApiResponse<Product>> getIpad() {
        Product product = productService.getProductByName("iPad")
                .orElseThrow(() -> new ResourceNotFoundException("Product", "name", "iPad"));
        
        validateAccess(product);
        return ResponseEntity.ok(ApiResponse.success("iPad retrieved successfully", product));
    }
    
    /**
     * Gets a specific MacBook Pro product (accessible by Admin only).
     *
     * @return the MacBook Pro product
     */
    @GetMapping("/macbook")
    public ResponseEntity<ApiResponse<Product>> getMacbook() {
        Product product = productService.getProductByName("MacBook Pro")
                .orElseThrow(() -> new ResourceNotFoundException("Product", "name", "MacBook Pro"));
        
        validateAccess(product);
        return ResponseEntity.ok(ApiResponse.success("MacBook Pro retrieved successfully", product));
    }
    
    /**
     * Creates a new product (admin only).
     *
     * @param product the product to create
     * @return the created product
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.ok(ApiResponse.success("Product created successfully", createdProduct));
    }
    
    /**
     * Adds a role to a product's allowed roles (admin only).
     *
     * @param productId the product ID
     * @param role the role to add
     * @return the updated product
     */
    @PostMapping("/{productId}/roles/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Product>> addRoleToProduct(
            @PathVariable String productId,
            @PathVariable String role) {
        
        Product updatedProduct = productService.addRoleToProduct(productId, role);
        return ResponseEntity.ok(ApiResponse.success("Role added to product successfully", updatedProduct));
    }
    
    /**
     * Removes a role from a product's allowed roles (admin only).
     *
     * @param productId the product ID
     * @param role the role to remove
     * @return the updated product
     */
    @DeleteMapping("/{productId}/roles/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Product>> removeRoleFromProduct(
            @PathVariable String productId,
            @PathVariable String role) {
        
        Product updatedProduct = productService.removeRoleFromProduct(productId, role);
        return ResponseEntity.ok(ApiResponse.success("Role removed from product successfully", updatedProduct));
    }
    
    /**
     * Validates if the current user has access to a product.
     *
     * @param product the product to check
     * @throws UnauthorizedException if the user does not have access
     */
    private void validateAccess(Product product) {
        Collection<String> roles = securityUtils.getCurrentUserRoles();
        boolean hasAccess = roles.contains("ADMIN") || 
                roles.stream().anyMatch(role -> product.getAllowedRoles().contains(role));
        
        if (!hasAccess) {
            throw new UnauthorizedException("access", "product");
        }
    }
} 