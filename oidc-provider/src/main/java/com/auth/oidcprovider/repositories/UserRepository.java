package com.auth.oidcprovider.repositories;

import com.auth.oidcprovider.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByKeycloakId(String keycloakId);
    
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByKeycloakId(String keycloakId);
    
    @Query("{ 'roles': ?0 }")
    List<User> findByRole(String role);
    
    @Query("{ 'active': true }")
    List<User> findAllActiveUsers();
}