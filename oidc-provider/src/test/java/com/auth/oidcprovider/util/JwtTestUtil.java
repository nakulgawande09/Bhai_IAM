package com.auth.oidcprovider.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Utility class for generating JWT tokens for testing.
 */
@Component
public class JwtTestUtil {

    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * Generates a JWT token with the specified subject and roles.
     *
     * @param userId the user ID to use as the subject
     * @param username the username to include in the token
     * @param roles the roles to include in the token
     * @return the generated JWT token
     */
    public String generateToken(String userId, String username, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        
        // Add standard claims
        claims.put("sub", userId);
        claims.put("preferred_username", username);
        claims.put("email", username + "@example.com");
        
        // Add Keycloak-style realm roles
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", roles);
        claims.put("realm_access", realmAccess);
        
        // Add Keycloak-style resource access (client roles)
        Map<String, Object> resourceAccess = new HashMap<>();
        Map<String, Object> oidcProviderClient = new HashMap<>();
        oidcProviderClient.put("roles", roles);
        resourceAccess.put("oidc-provider", oidcProviderClient);
        claims.put("resource_access", resourceAccess);
        
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
                .signWith(SECRET_KEY)
                .compact();
    }
    
    /**
     * Generates a JWT token with Admin role.
     *
     * @param userId the user ID
     * @param username the username
     * @return the generated admin JWT token
     */
    public String generateAdminToken(String userId, String username) {
        return generateToken(userId, username, Collections.singletonList("ADMIN"));
    }
    
    /**
     * Generates a JWT token with Sales Manager role.
     *
     * @param userId the user ID
     * @param username the username
     * @return the generated sales manager JWT token
     */
    public String generateSalesManagerToken(String userId, String username) {
        return generateToken(userId, username, Collections.singletonList("SALES_MANAGER"));
    }
    
    /**
     * Generates a JWT token with CRM Rep role.
     *
     * @param userId the user ID
     * @param username the username
     * @return the generated CRM rep JWT token
     */
    public String generateCrmRepToken(String userId, String username) {
        return generateToken(userId, username, Collections.singletonList("CRM_REP"));
    }
    
    /**
     * Gets the signing key used for test tokens.
     *
     * @return the signing key
     */
    public SecretKey getSigningKey() {
        return SECRET_KEY;
    }
} 