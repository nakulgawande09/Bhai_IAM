package com.auth.oidcprovider.services;

import java.util.Map;

/**
 * Service for Keycloak operations
 */
public interface KeycloakService {
    
    /**
     * Get user information from a token
     * @param token JWT token
     * @return Map containing user information
     */
    Map<String, Object> getUserInfo(String token);
    
    /**
     * Refresh an access token using a refresh token
     * @param refreshToken Refresh token
     * @return Map containing new access token and refresh token
     */
    Map<String, String> refreshToken(String refreshToken);
    
    /**
     * Validate a token
     * @param token JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean validateToken(String token);
} 