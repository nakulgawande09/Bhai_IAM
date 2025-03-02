package com.auth.oidcprovider.repositories;

import com.auth.oidcprovider.models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entities.
 */
@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    
    /**
     * Finds a product by its name.
     *
     * @param name the product name
     * @return the product if found
     */
    Optional<Product> findByName(String name);
    
    /**
     * Finds all products that have the specified role in their allowed roles.
     *
     * @param role the role to check for
     * @return list of products accessible by the role
     */
    List<Product> findByAllowedRolesContaining(String role);
} 