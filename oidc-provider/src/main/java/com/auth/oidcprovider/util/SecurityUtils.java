package com.auth.oidcprovider.util;

import com.auth.oidcprovider.exceptions.InvalidTokenException;
import com.auth.oidcprovider.models.User;
import com.auth.oidcprovider.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Utility class for security operations such as retrieving the current authenticated user
 * and checking roles and permissions.
 */
public class SecurityUtils {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    public SecurityUtils(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * Gets the current authenticated user as a User entity.
     * 
     * @return the authenticated user
     * @throws InvalidTokenException if the token is invalid or no authentication is present
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            throw new InvalidTokenException("No authentication present");
        }
        
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authentication;
            Jwt jwt = jwtToken.getToken();
            String keycloakId = jwtUtil.getSubjectFromToken(jwt);
            
            return userService.findByKeycloakId(keycloakId)
                    .orElseThrow(() -> new InvalidTokenException("User not found for authenticated token"));
        }
        
        throw new InvalidTokenException("Invalid authentication type");
    }
    
    /**
     * Gets the current user as an Optional to handle cases where a user might not be authenticated.
     * 
     * @return Optional containing the authenticated user, or empty if not authenticated
     */
    public Optional<User> getCurrentUserOptional() {
        try {
            return Optional.of(getCurrentUser());
        } catch (Exception e) {
            logger.debug("Failed to get current user: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Checks if the current authenticated user has the specified role.
     * 
     * @param role the role to check
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            return false;
        }
        
        String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase();
        
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(roleWithPrefix));
    }
    
    /**
     * Gets all roles of the current authenticated user.
     * 
     * @return a collection of role names without the "ROLE_" prefix
     */
    public Collection<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            return java.util.Collections.emptyList();
        }
        
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring(5)) // Remove "ROLE_" prefix
                .collect(Collectors.toList());
    }
    
    /**
     * Checks if the current authentication represents an authenticated user.
     * 
     * @return true if authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
    
    /**
     * Checks if the current user has the ADMIN role.
     *
     * @return true if the user has the ADMIN role, false otherwise
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }
    
    /**
     * Checks if the current user is the owner of a resource based on ID.
     *
     * @param resourceId the ID of the resource
     * @return true if the current user is the owner, false otherwise
     */
    public boolean isResourceOwner(String resourceId) {
        try {
            User currentUser = getCurrentUser();
            return currentUser.getId().equals(resourceId);
        } catch (Exception e) {
            logger.debug("Failed to check resource ownership: {}", e.getMessage());
            return false;
        }
    }
} 