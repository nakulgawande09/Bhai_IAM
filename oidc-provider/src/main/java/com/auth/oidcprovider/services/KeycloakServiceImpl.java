package com.auth.oidcprovider.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class KeycloakServiceImpl implements KeycloakService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakServiceImpl.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${keycloak.realm}")
    private String realm;
    
    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;
    
    @Value("${keycloak.resource}")
    private String clientId;
    
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;
    
    public KeycloakServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public Map<String, Object> getUserInfo(String token) {
        if (!StringUtils.hasText(token)) {
            log.error("Token cannot be null or empty");
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        
        try {
            // First, validate the token
            if (!validateToken(token)) {
                log.error("Invalid token provided");
                throw new IllegalArgumentException("Invalid token provided");
            }
            
            // Parse the JWT payload to get user information
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                log.error("Invalid JWT token format");
                throw new IllegalArgumentException("Invalid JWT token format");
            }
            
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> claims = objectMapper.readValue(payload, Map.class);
                log.debug("Successfully extracted user info from token");
                return claims;
            } catch (JsonProcessingException e) {
                log.error("Failed to parse JWT payload", e);
                throw new IllegalArgumentException("Failed to parse JWT payload", e);
            }
        } catch (Exception e) {
            log.error("Error getting user info from token", e);
            throw new RuntimeException("Error getting user info from token", e);
        }
    }
    
    @Override
    public Map<String, String> refreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            log.error("Refresh token cannot be null or empty");
            throw new IllegalArgumentException("Refresh token cannot be null or empty");
        }
        
        try {
            String tokenEndpoint = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", clientId);
            map.add("grant_type", "refresh_token");
            map.add("refresh_token", refreshToken);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                tokenEndpoint,
                HttpMethod.POST,
                request,
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> body = response.getBody();
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", (String) body.get("access_token"));
                tokens.put("refresh_token", (String) body.get("refresh_token"));
                
                log.debug("Successfully refreshed token");
                return tokens;
            } else {
                log.error("Failed to refresh token. Status: {}", response.getStatusCode());
                throw new RuntimeException("Failed to refresh token");
            }
        } catch (RestClientException e) {
            log.error("Error refreshing token", e);
            throw new RuntimeException("Error refreshing token", e);
        }
    }
    
    @Override
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            log.error("Token cannot be null or empty");
            return false;
        }
        
        try {
            String introspectEndpoint = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token/introspect";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", clientId);
            map.add("token", token);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                introspectEndpoint,
                HttpMethod.POST,
                request,
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> body = response.getBody();
                boolean active = (boolean) body.getOrDefault("active", false);
                
                if (active) {
                    log.debug("Token is valid");
                } else {
                    log.debug("Token is invalid or expired");
                }
                
                return active;
            } else {
                log.error("Failed to validate token. Status: {}", response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            log.error("Error validating token", e);
            return false;
        }
    }
} 