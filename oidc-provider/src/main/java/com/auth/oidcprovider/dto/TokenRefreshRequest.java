package com.auth.oidcprovider.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

public class TokenRefreshRequest {
    
    @NotBlank(message = "Refresh token cannot be blank")
    private String refreshToken;
    
    public TokenRefreshRequest() {
        // Default constructor
    }
    
    public TokenRefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenRefreshRequest that = (TokenRefreshRequest) o;
        return Objects.equals(refreshToken, that.refreshToken);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(refreshToken);
    }
    
    @Override
    public String toString() {
        return "TokenRefreshRequest{" +
                "refreshToken='" + refreshToken + '\'' +
                '}';
    }
    
    public static TokenRefreshRequestBuilder builder() {
        return new TokenRefreshRequestBuilder();
    }
    
    public static class TokenRefreshRequestBuilder {
        private String refreshToken;
        
        public TokenRefreshRequestBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }
        
        public TokenRefreshRequest build() {
            return new TokenRefreshRequest(refreshToken);
        }
    }
} 