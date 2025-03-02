package com.auth.oidcprovider.util;

import com.auth.oidcprovider.models.Product;
import com.auth.oidcprovider.models.User;
import com.auth.oidcprovider.repositories.ProductRepository;
import com.auth.oidcprovider.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

/**
 * Utility class for initializing test data.
 */
@Component
public class TestDataInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(TestDataInitializer.class);
    
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    public TestDataInitializer(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * Initializes products for testing.
     */
    public void initializeProducts() {
        logger.info("Initializing test products...");
        
        // Clear existing products
        productRepository.deleteAll();
        
        // Create iPhone
        Product iPhone = new Product("iPhone", "Apple iPhone 14 Pro", 999.99);
        
        // Create iPad
        Product iPad = new Product("iPad", "Apple iPad Pro", 799.99);
        
        // Create MacBook Pro
        Product macbookPro = new Product("MacBook Pro", "Apple MacBook Pro 16", 2499.99);
        
        // Save all products
        productRepository.saveAll(Arrays.asList(iPhone, iPad, macbookPro));
        
        logger.info("Test products initialized successfully");
    }
    
    /**
     * Sets up roles for products.
     */
    public void setupProductRoles() {
        logger.info("Setting up product roles...");
        
        // Get products
        Product iPhone = productRepository.findByName("iPhone").orElseThrow();
        Product iPad = productRepository.findByName("iPad").orElseThrow();
        Product macbookPro = productRepository.findByName("MacBook Pro").orElseThrow();
        
        // Set up roles
        // CRM Rep can access iPhone
        iPhone.addAllowedRole("CRM_REP");
        
        // Sales Manager can access iPhone and iPad
        iPhone.addAllowedRole("SALES_MANAGER");
        iPad.addAllowedRole("SALES_MANAGER");
        
        // Admin can access all products
        iPhone.addAllowedRole("ADMIN");
        iPad.addAllowedRole("ADMIN");
        macbookPro.addAllowedRole("ADMIN");
        
        // Save products with updated roles
        productRepository.saveAll(Arrays.asList(iPhone, iPad, macbookPro));
        
        logger.info("Product roles set up successfully");
    }
    
    /**
     * Creates a test user with the specified roles.
     *
     * @param username the username
     * @param roles the roles to assign
     * @return the created user
     */
    public User createTestUser(String username, String... roles) {
        logger.info("Creating test user: {} with roles: {}", username, Arrays.toString(roles));
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setActive(true);
        user.setKeycloakId(UUID.randomUUID().toString());
        user.setRoles(Arrays.asList(roles));
        
        return userRepository.save(user);
    }
    
    /**
     * Updates a user's roles.
     *
     * @param userId the user ID
     * @param roles the new roles
     * @return the updated user
     */
    public User updateUserRoles(String userId, String... roles) {
        logger.info("Updating roles for user ID: {} to: {}", userId, Arrays.toString(roles));
        
        User user = userRepository.findById(userId).orElseThrow();
        user.setRoles(Arrays.asList(roles));
        
        return userRepository.save(user);
    }
    
    /**
     * Cleans up all test data.
     */
    public void cleanupTestData() {
        logger.info("Cleaning up test data...");
        productRepository.deleteAll();
        userRepository.deleteAll();
        logger.info("Test data cleaned up successfully");
    }
} 