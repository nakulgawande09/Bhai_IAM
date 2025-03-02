package com.auth.oidcprovider.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Objects;

public class UpdateUserRequest {
    
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @Email(message = "Email must be valid")
    private String email;
    
    private List<String> roles;
    
    private Boolean active;
    
    // No keycloakId as it shouldn't be updatable
    
    public UpdateUserRequest() {
        // Default constructor
    }
    
    public UpdateUserRequest(String username, String email, List<String> roles, Boolean active) {
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.active = active;
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
    
    public List<String> getRoles() {
        return roles;
    }
    
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateUserRequest that = (UpdateUserRequest) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(email, that.email) &&
                Objects.equals(roles, that.roles) &&
                Objects.equals(active, that.active);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(username, email, roles, active);
    }
    
    @Override
    public String toString() {
        return "UpdateUserRequest{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                ", active=" + active +
                '}';
    }
    
    public static UpdateUserRequestBuilder builder() {
        return new UpdateUserRequestBuilder();
    }
    
    public static class UpdateUserRequestBuilder {
        private String username;
        private String email;
        private List<String> roles;
        private Boolean active;
        
        public UpdateUserRequestBuilder username(String username) {
            this.username = username;
            return this;
        }
        
        public UpdateUserRequestBuilder email(String email) {
            this.email = email;
            return this;
        }
        
        public UpdateUserRequestBuilder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }
        
        public UpdateUserRequestBuilder active(Boolean active) {
            this.active = active;
            return this;
        }
        
        public UpdateUserRequest build() {
            return new UpdateUserRequest(username, email, roles, active);
        }
    }
} 