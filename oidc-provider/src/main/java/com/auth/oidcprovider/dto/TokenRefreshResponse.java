package com.auth.oidcprovider.dto;

import java.util.Objects;

public class TokenRefreshResponse {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    
    public TokenRefreshResponse() {
        // Default constructor
    }
    
    public TokenRefreshResponse(String accessToken, String refreshToken, String tokenType) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenRefreshResponse that = (TokenRefreshResponse) o;
        return Objects.equals(accessToken, that.accessToken) &&
                Objects.equals(refreshToken, that.refreshToken) &&
                Objects.equals(tokenType, that.tokenType);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(accessToken, refreshToken, tokenType);
    }
    
    @Override
    public String toString() {
        return "TokenRefreshResponse{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", tokenType='" + tokenType + '\'' +
                '}';
    }
    
    public static TokenRefreshResponse of(String accessToken, String refreshToken) {
        return new TokenRefreshResponseBuilder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }
    
    public static TokenRefreshResponseBuilder builder() {
        return new TokenRefreshResponseBuilder();
    }
    
    public static class TokenRefreshResponseBuilder {
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
        
        public TokenRefreshResponseBuilder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }
        
        public TokenRefreshResponseBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }
        
        public TokenRefreshResponseBuilder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }
        
        public TokenRefreshResponse build() {
            return new TokenRefreshResponse(accessToken, refreshToken, tokenType);
        }
    }
} 