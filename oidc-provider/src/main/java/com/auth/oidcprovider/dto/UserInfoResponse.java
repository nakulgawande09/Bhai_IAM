package com.auth.oidcprovider.dto;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserInfoResponse {
    
    private String id;
    private String sub;  // Subject identifier (Keycloak ID)
    private String username;
    private String email;
    private boolean emailVerified;
    private List<String> roles;
    private Map<String, Object> additionalClaims;
    
    public UserInfoResponse() {
    }
    
    public UserInfoResponse(String id, String sub, String username, String email, 
                          boolean emailVerified, List<String> roles, 
                          Map<String, Object> additionalClaims) {
        this.id = id;
        this.sub = sub;
        this.username = username;
        this.email = email;
        this.emailVerified = emailVerified;
        this.roles = roles;
        this.additionalClaims = additionalClaims;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getSub() {
        return sub;
    }
    
    public void setSub(String sub) {
        this.sub = sub;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public boolean isEmailVerified() {
        return emailVerified;
    }
    
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
    
    public List<String> getRoles() {
        return roles;
    }
    
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    
    public Map<String, Object> getAdditionalClaims() {
        return additionalClaims;
    }
    
    public void setAdditionalClaims(Map<String, Object> additionalClaims) {
        this.additionalClaims = additionalClaims;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        UserInfoResponse that = (UserInfoResponse) o;
        
        return emailVerified == that.emailVerified &&
                Objects.equals(id, that.id) &&
                Objects.equals(sub, that.sub) &&
                Objects.equals(username, that.username) &&
                Objects.equals(email, that.email) &&
                Objects.equals(roles, that.roles) &&
                Objects.equals(additionalClaims, that.additionalClaims);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, sub, username, email, emailVerified, roles, additionalClaims);
    }
    
    @Override
    public String toString() {
        return "UserInfoResponse{" +
                "id='" + id + '\'' +
                ", sub='" + sub + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", emailVerified=" + emailVerified +
                ", roles=" + roles +
                ", additionalClaims=" + additionalClaims +
                '}';
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String id;
        private String sub;
        private String username;
        private String email;
        private boolean emailVerified;
        private List<String> roles;
        private Map<String, Object> additionalClaims;
        
        private Builder() {
        }
        
        public Builder id(String id) {
            this.id = id;
            return this;
        }
        
        public Builder sub(String sub) {
            this.sub = sub;
            return this;
        }
        
        public Builder username(String username) {
            this.username = username;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Builder emailVerified(boolean emailVerified) {
            this.emailVerified = emailVerified;
            return this;
        }
        
        public Builder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }
        
        public Builder additionalClaims(Map<String, Object> additionalClaims) {
            this.additionalClaims = additionalClaims;
            return this;
        }
        
        public UserInfoResponse build() {
            return new UserInfoResponse(id, sub, username, email, emailVerified, roles, additionalClaims);
        }
    }
} 