package com.auth.oidcprovider.services;

import com.auth.oidcprovider.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    /**
     * Find all users in the system
     * @return List of all users
     */
    List<User> findAllUsers();
    
    /**
     * Find a user by their ID
     * @param id User ID
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findUserById(String id);
    
    /**
     * Find a user by their Keycloak ID
     * @param keycloakId Keycloak ID
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByKeycloakId(String keycloakId);
    
    /**
     * Find a user by their username
     * @param username Username
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findUserByUsername(String username);
    
    /**
     * Create or update a user
     * @param user User to save
     * @return Saved user
     */
    User saveUser(User user);
    
    /**
     * Delete a user by their ID
     * @param id User ID
     */
    void deleteUser(String id);
} 