package com.auth.oidcprovider.services;

import com.auth.oidcprovider.exceptions.ResourceNotFoundException;
import com.auth.oidcprovider.models.Product;
import com.auth.oidcprovider.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the ProductService interface.
 */
@Service
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    @Override
    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }
    
    @Override
    public Optional<Product> getProductByName(String name) {
        return productRepository.findByName(name);
    }
    
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    @Override
    public List<Product> getProductsByRole(String role) {
        return productRepository.findByAllowedRolesContaining(role);
    }
    
    @Override
    public Product addRoleToProduct(String productId, String role) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        product.addAllowedRole(role);
        return productRepository.save(product);
    }
    
    @Override
    public Product removeRoleFromProduct(String productId, String role) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        product.getAllowedRoles().remove(role);
        return productRepository.save(product);
    }
    
    @Override
    public boolean isProductAccessibleByRole(String productId, String role) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        
        return product.isAccessibleByRole(role) || role.equals("ADMIN");
    }
} 