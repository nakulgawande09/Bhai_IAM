package com.auth.oidcprovider.models;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

@Document(collection = "users")
public class User extends BaseEntity {
    
    @Indexed(unique = true)
    private String username;
    
    @Indexed(unique = true)
    private String email;
    
    @Indexed(unique = true)
    private String keycloakId;
    
    private List<String> roles;
    private boolean active;
    
    public User() {
        // Default constructor
    }
    
    public User(String username, String email, String keycloakId, List<String> roles, boolean active) {
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
        if (!super.equals(o)) return false;
        User user = (User) o;
        return active == user.active &&
                Objects.equals(username, user.username) &&
                Objects.equals(email, user.email) &&
                Objects.equals(keycloakId, user.keycloakId) &&
                Objects.equals(roles, user.roles);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username, email, keycloakId, roles, active);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id='" + getId() + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", keycloakId='" + keycloakId + '\'' +
                ", roles=" + roles +
                ", active=" + active +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
} 