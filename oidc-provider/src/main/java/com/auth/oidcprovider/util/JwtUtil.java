package com.auth.oidcprovider.util;

import com.auth.oidcprovider.exceptions.InvalidTokenException;
import com.auth.oidcprovider.dto.UserInfoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for handling JWT tokens and claims extraction.
 */
@Component
public class JwtUtil {
    
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
    private static final String ROLES_CLAIM = "realm_access.roles";
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String PREFERRED_USERNAME = "preferred_username";
    private static final String EMAIL = "email";
    private static final String EMAIL_VERIFIED = "email_verified";
    
    private final JwtDecoder jwtDecoder;
    
    @Autowired
    public JwtUtil(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }
    
    /**
     * Validates a JWT token and checks if it's not expired.
     *
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            // Remove "Bearer " prefix if present
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // The decode method validates the token signature and expiration
            jwtDecoder.decode(token);
            return true;
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Error validating JWT token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Extracts the subject (user ID) from a JWT token.
     *
     * @param token the JWT token
     * @return the subject from the token
     * @throws InvalidTokenException if the token is invalid
     */
    public String getSubjectFromToken(String token) {
        try {
            // Remove "Bearer " prefix if present
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getSubject();
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token");
        }
    }
    
    /**
     * Extracts the subject (user ID) from a JWT object.
     *
     * @param jwt the JWT object
     * @return the subject from the token
     */
    public String getSubjectFromToken(Jwt jwt) {
        return jwt.getSubject();
    }
    
    /**
     * Extracts a specific claim from a JWT token.
     *
     * @param token the JWT token
     * @param claimName the name of the claim to extract
     * @return the value of the claim, or null if not present
     * @throws InvalidTokenException if the token is invalid
     */
    public Object getClaimFromToken(String token, String claimName) {
        try {
            // Remove "Bearer " prefix if present
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getClaim(claimName);
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token");
        }
    }
    
    /**
     * Extracts a nested claim from a JWT token using dot notation.
     * For example, "realm_access.roles" would extract the roles array from the realm_access object.
     *
     * @param token the JWT token
     * @param claimPath the path to the claim using dot notation
     * @return the value of the nested claim, or null if not present
     * @throws InvalidTokenException if the token is invalid
     */
    @SuppressWarnings("unchecked")
    public Object getNestedClaimFromToken(String token, String claimPath) {
        try {
            // Remove "Bearer " prefix if present
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Jwt jwt = jwtDecoder.decode(token);
            String[] pathParts = claimPath.split("\\.");
            
            Object currentObject = jwt.getClaims();
            for (String part : pathParts) {
                if (currentObject instanceof Map) {
                    currentObject = ((Map<String, Object>) currentObject).get(part);
                    if (currentObject == null) {
                        return null;
                    }
                } else {
                    return null;
                }
            }
            
            return currentObject;
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token");
        }
    }
    
    /**
     * Extracts the roles from a JWT token.
     *
     * @param token the JWT token
     * @return a list of roles from the token
     * @throws InvalidTokenException if the token is invalid
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        try {
            // Remove "Bearer " prefix if present
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Jwt jwt = jwtDecoder.decode(token);
            List<String> roles = new ArrayList<>();
            
            // Extract realm roles
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                roles.addAll((List<String>) realmAccess.get("roles"));
            }
            
            // Extract client roles
            Map<String, Object> resourceAccess = jwt.getClaim(RESOURCE_ACCESS);
            if (resourceAccess != null) {
                resourceAccess.forEach((clientId, clientAccess) -> {
                    if (clientAccess instanceof Map) {
                        Map<String, Object> clientAccessMap = (Map<String, Object>) clientAccess;
                        if (clientAccessMap.containsKey("roles")) {
                            roles.addAll(((List<String>) clientAccessMap.get("roles")).stream()
                                    .map(role -> clientId + ":" + role)
                                    .collect(Collectors.toList()));
                        }
                    }
                });
            }
            
            return roles;
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token");
        }
    }
    
    /**
     * Converts JWT token roles to Spring Security GrantedAuthority objects.
     *
     * @param token the JWT token
     * @return a collection of granted authorities
     * @throws InvalidTokenException if the token is invalid
     */
    public Collection<GrantedAuthority> getAuthoritiesFromToken(String token) {
        List<String> roles = getRolesFromToken(token);
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * Extracts user information from a JWT token.
     *
     * @param token the JWT token
     * @return a UserInfoResponse object containing user information
     * @throws InvalidTokenException if the token is invalid
     */
    @SuppressWarnings("unchecked")
    public UserInfoResponse getUserInfoFromToken(String token) {
        try {
            // Remove "Bearer " prefix if present
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Jwt jwt = jwtDecoder.decode(token);
            List<String> roles = getRolesFromToken(token);
            
            // Extract basic user info
            String sub = jwt.getSubject();
            String username = jwt.getClaimAsString(PREFERRED_USERNAME);
            String email = jwt.getClaimAsString(EMAIL);
            Boolean emailVerified = jwt.getClaim(EMAIL_VERIFIED);
            
            // Extract all remaining claims
            Map<String, Object> additionalClaims = new HashMap<>(jwt.getClaims());
            // Remove standard claims to keep only additional ones
            Arrays.asList("sub", PREFERRED_USERNAME, EMAIL, EMAIL_VERIFIED, "exp", "iat", "auth_time", 
                    "jti", "iss", "aud", "typ", "azp", "session_state", "acr", "realm_access", RESOURCE_ACCESS)
                    .forEach(additionalClaims::remove);
            
            return UserInfoResponse.builder()
                    .sub(sub)
                    .username(username)
                    .email(email)
                    .emailVerified(emailVerified != null && emailVerified)
                    .roles(roles)
                    .additionalClaims(additionalClaims)
                    .build();
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token");
        }
    }
} 