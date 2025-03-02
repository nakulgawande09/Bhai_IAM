package com.auth.oidcprovider.controllers;

import com.auth.oidcprovider.dto.ApiResponse;
import com.auth.oidcprovider.dto.TokenRefreshRequest;
import com.auth.oidcprovider.dto.TokenRefreshResponse;
import com.auth.oidcprovider.services.KeycloakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth Controller", description = "APIs for authentication operations")
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final KeycloakService keycloakService;
    
    public AuthController(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }
    
    @GetMapping("/userinfo")
    @Operation(
        summary = "Get user info", 
        description = "Retrieves information about the current authenticated user from their JWT token",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserInfo(Authentication authentication) {
        log.debug("REST request to get user info from token");
        
        if (!(authentication instanceof JwtAuthenticationToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Not authenticated with a valid token"));
        }
        
        String token = ((JwtAuthenticationToken) authentication).getToken().getTokenValue();
        Map<String, Object> userInfo = keycloakService.getUserInfo(token);
        
        return ResponseEntity.ok(ApiResponse.success("User info retrieved successfully", userInfo));
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Refresh an access token using a refresh token")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(
            @Valid @RequestBody TokenRefreshRequest request) {
        log.debug("REST request to refresh token");
        
        try {
            Map<String, String> tokens = keycloakService.refreshToken(request.getRefreshToken());
            
            TokenRefreshResponse response = TokenRefreshResponse.of(
                    tokens.get("access_token"),
                    tokens.get("refresh_token")
            );
            
            return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
        } catch (Exception e) {
            log.error("Error refreshing token", e);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, 
                    "Failed to refresh token: " + e.getMessage());
        }
    }
    
    @PostMapping("/logout")
    @Operation(
        summary = "Logout", 
        description = "Invalidates the current token",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ApiResponse<Void>> logout(
            Authentication authentication, 
            HttpServletRequest request) {
        log.debug("REST request to logout");
        
        if (!(authentication instanceof JwtAuthenticationToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Not authenticated with a valid token"));
        }
        
        // In a stateless JWT setup, we can't actually invalidate the token from the server side
        // The client should discard the token
        // For a more comprehensive solution, we would need to:
        // 1. Add the token to a blacklist
        // 2. Implement a filter to check tokens against the blacklist
        
        log.info("User logged out");
        return ResponseEntity.ok(ApiResponse.success("Successfully logged out", null));
    }
} 