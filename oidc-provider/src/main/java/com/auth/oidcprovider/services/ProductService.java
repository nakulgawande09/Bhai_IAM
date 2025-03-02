package com.auth.oidcprovider.services;

import com.auth.oidcprovider.models.Product;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for handling product operations.
 */
public interface ProductService {
    
    /**
     * Creates a new product.
     *
     * @param product the product to create
     * @return the created product
     */
    Product createProduct(Product product);
    
    /**
     * Gets a product by its ID.
     *
     * @param id the product ID
     * @return the product if found
     */
    Optional<Product> getProductById(String id);
    
    /**
     * Gets a product by its name.
     *
     * @param name the product name
     * @return the product if found
     */
    Optional<Product> getProductByName(String name);
    
    /**
     * Gets all products.
     *
     * @return list of all products
     */
    List<Product> getAllProducts();
    
    /**
     * Gets products accessible by the specified role.
     *
     * @param role the role
     * @return list of products accessible by the role
     */
    List<Product> getProductsByRole(String role);
    
    /**
     * Adds a role to the allowed roles for a product.
     *
     * @param productId the product ID
     * @param role the role to add
     * @return the updated product
     */
    Product addRoleToProduct(String productId, String role);
    
    /**
     * Removes a role from the allowed roles for a product.
     *
     * @param productId the product ID
     * @param role the role to remove
     * @return the updated product
     */
    Product removeRoleFromProduct(String productId, String role);
    
    /**
     * Checks if a product is accessible by a specific role.
     *
     * @param productId the product ID
     * @param role the role to check
     * @return true if the product is accessible by the role
     */
    boolean isProductAccessibleByRole(String productId, String role);
} 