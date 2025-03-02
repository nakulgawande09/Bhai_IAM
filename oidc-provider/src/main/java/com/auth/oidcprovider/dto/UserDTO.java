package com.auth.oidcprovider.dto;

import com.auth.oidcprovider.models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class UserDTO {
    
    private String id;
    
    @NotBlank(message = "Username cannot be blank")
    private String username;
    
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;
    
    private String keycloakId;
    
    @NotEmpty(message = "At least one role is required")
    private List<String> roles;
    
    private boolean active;
    
    private Date createdAt;
    private Date updatedAt;
    
    public UserDTO() {
        // Default constructor
    }
    
    public UserDTO(String id, String username, String email, String keycloakId, List<String> roles, boolean active, Date createdAt, Date updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.keycloakId = keycloakId;
        this.roles = roles;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
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
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return active == userDTO.active &&
                Objects.equals(id, userDTO.id) &&
                Objects.equals(username, userDTO.username) &&
                Objects.equals(email, userDTO.email) &&
                Objects.equals(keycloakId, userDTO.keycloakId) &&
                Objects.equals(roles, userDTO.roles) &&
                Objects.equals(createdAt, userDTO.createdAt) &&
                Objects.equals(updatedAt, userDTO.updatedAt);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, keycloakId, roles, active, createdAt, updatedAt);
    }
    
    @Override
    public String toString() {
        return "UserDTO{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", keycloakId='" + keycloakId + '\'' +
                ", roles=" + roles +
                ", active=" + active +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
    
    public static UserDTOBuilder builder() {
        return new UserDTOBuilder();
    }
    
    public static class UserDTOBuilder {
        private String id;
        private String username;
        private String email;
        private String keycloakId;
        private List<String> roles;
        private boolean active;
        private Date createdAt;
        private Date updatedAt;
        
        public UserDTOBuilder id(String id) {
            this.id = id;
            return this;
        }
        
        public UserDTOBuilder username(String username) {
            this.username = username;
            return this;
        }
        
        public UserDTOBuilder email(String email) {
            this.email = email;
            return this;
        }
        
        public UserDTOBuilder keycloakId(String keycloakId) {
            this.keycloakId = keycloakId;
            return this;
        }
        
        public UserDTOBuilder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }
        
        public UserDTOBuilder active(boolean active) {
            this.active = active;
            return this;
        }
        
        public UserDTOBuilder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public UserDTOBuilder updatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        
        public UserDTO build() {
            return new UserDTO(id, username, email, keycloakId, roles, active, createdAt, updatedAt);
        }
    }
} 