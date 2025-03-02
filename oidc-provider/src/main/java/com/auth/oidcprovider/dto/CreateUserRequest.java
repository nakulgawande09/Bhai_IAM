package com.auth.oidcprovider.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Objects;

public class CreateUserRequest {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank(message = "Keycloak ID is required")
    private String keycloakId;
    
    @NotEmpty(message = "At least one role is required")
    private List<String> roles;
    
    private boolean active = true;
    
    public CreateUserRequest() {
        // Default constructor
    }
    
    public CreateUserRequest(String username, String email, String keycloakId, List<String> roles, boolean active) {
        this.username = username;
        this.email = email;
        this.keycloakId = keycloakId;
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
    
    public String getKeycloakId() {
        return keycloakId;
    }
    
    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }
    
    public List<String> getRoles() {
        return roles;
    }
    
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateUserRequest that = (CreateUserRequest) o;
        return active == that.active &&
                Objects.equals(username, that.username) &&
                Objects.equals(email, that.email) &&
                Objects.equals(keycloakId, that.keycloakId) &&
                Objects.equals(roles, that.roles);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(username, email, keycloakId, roles, active);
    }
    
    @Override
    public String toString() {
        return "CreateUserRequest{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", keycloakId='" + keycloakId + '\'' +
                ", roles=" + roles +
                ", active=" + active +
                '}';
    }
    
    public static CreateUserRequestBuilder builder() {
        return new CreateUserRequestBuilder();
    }
    
    public static class CreateUserRequestBuilder {
        private String username;
        private String email;
        private String keycloakId;
        private List<String> roles;
        private boolean active = true;
        
        public CreateUserRequestBuilder username(String username) {
            this.username = username;
            return this;
        }
        
        public CreateUserRequestBuilder email(String email) {
            this.email = email;
            return this;
        }
        
        public CreateUserRequestBuilder keycloakId(String keycloakId) {
            this.keycloakId = keycloakId;
            return this;
        }
        
        public CreateUserRequestBuilder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }
        
        public CreateUserRequestBuilder active(boolean active) {
            this.active = active;
            return this;
        }
        
        public CreateUserRequest build() {
            return new CreateUserRequest(username, email, keycloakId, roles, active);
        }
    }
} 