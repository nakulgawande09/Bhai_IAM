package com.auth.oidcprovider.services;

import com.auth.oidcprovider.models.User;
import com.auth.oidcprovider.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        log.debug("Finding all users");
        return userRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserById(String id) {
        log.debug("Finding user by ID: {}", id);
        if (!StringUtils.hasText(id)) {
            log.error("User ID is null or empty");
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        return userRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        if (!StringUtils.hasText(username)) {
            log.error("Username is null or empty");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        return userRepository.findByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByKeycloakId(String keycloakId) {
        log.debug("Finding user by Keycloak ID: {}", keycloakId);
        if (!StringUtils.hasText(keycloakId)) {
            log.error("Keycloak ID is null or empty");
            throw new IllegalArgumentException("Keycloak ID cannot be null or empty");
        }
        return userRepository.findByKeycloakId(keycloakId);
    }
    
    @Override
    @Transactional
    public User saveUser(User user) {
        validateUser(user);
        
        if (user.getId() == null) {
            // This is a create operation
            // Check if user with same username or email already exists
            if (userRepository.existsByUsername(user.getUsername())) {
                log.error("Username already exists: {}", user.getUsername());
                throw new IllegalArgumentException("Username already exists: " + user.getUsername());
            }
            
            if (userRepository.existsByEmail(user.getEmail())) {
                log.error("Email already exists: {}", user.getEmail());
                throw new IllegalArgumentException("Email already exists: " + user.getEmail());
            }
            
            if (user.getKeycloakId() != null && userRepository.existsByKeycloakId(user.getKeycloakId())) {
                log.error("Keycloak ID already exists: {}", user.getKeycloakId());
                throw new IllegalArgumentException("Keycloak ID already exists: " + user.getKeycloakId());
            }
            
            log.info("Creating new user: {}", user.getUsername());
        } else {
            // This is an update operation
            // Check if user exists
            Optional<User> existingUserOpt = userRepository.findById(user.getId());
            if (existingUserOpt.isEmpty()) {
                log.error("User not found with ID: {}", user.getId());
                throw new IllegalArgumentException("User not found with ID: " + user.getId());
            }
            
            User existingUser = existingUserOpt.get();
            
            // Check for unique constraints only if the value has changed
            if (!existingUser.getUsername().equals(user.getUsername()) && 
                userRepository.existsByUsername(user.getUsername())) {
                log.error("Username already exists: {}", user.getUsername());
                throw new IllegalArgumentException("Username already exists: " + user.getUsername());
            }
            
            if (!existingUser.getEmail().equals(user.getEmail()) && 
                userRepository.existsByEmail(user.getEmail())) {
                log.error("Email already exists: {}", user.getEmail());
                throw new IllegalArgumentException("Email already exists: " + user.getEmail());
            }
            
            if (user.getKeycloakId() != null && 
                !user.getKeycloakId().equals(existingUser.getKeycloakId()) && 
                userRepository.existsByKeycloakId(user.getKeycloakId())) {
                log.error("Keycloak ID already exists: {}", user.getKeycloakId());
                throw new IllegalArgumentException("Keycloak ID already exists: " + user.getKeycloakId());
            }
            
            log.info("Updating user: {}", user.getUsername());
        }
        
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(String id) {
        log.debug("Deleting user with ID: {}", id);
        if (!StringUtils.hasText(id)) {
            log.error("User ID is null or empty");
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        
        if (!userRepository.existsById(id)) {
            log.error("User not found with ID: {}", id);
            throw new IllegalArgumentException("User not found with ID: " + id);
        }
        
        userRepository.deleteById(id);
        log.info("User deleted with ID: {}", id);
    }
    
    /**
     * Validates that a user has all required fields
     * 
     * @param user the user to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateUser(User user) {
        if (user == null) {
            log.error("User is null");
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (!StringUtils.hasText(user.getUsername())) {
            log.error("Username is null or empty");
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        
        if (!StringUtils.hasText(user.getEmail())) {
            log.error("Email is null or empty");
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            log.error("Roles are null or empty for user: {}", user.getUsername());
            throw new IllegalArgumentException("At least one role is required");
        }
    }
} 