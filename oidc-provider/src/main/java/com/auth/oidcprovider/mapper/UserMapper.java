package com.auth.oidcprovider.mapper;

import com.auth.oidcprovider.dto.CreateUserRequest;
import com.auth.oidcprovider.dto.UpdateUserRequest;
import com.auth.oidcprovider.dto.UserDTO;
import com.auth.oidcprovider.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    /**
     * Converts a User entity to a UserDTO
     * 
     * @param user the user entity to convert
     * @return the resulting UserDTO
     */
    public UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }
        
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .keycloakId(user.getKeycloakId())
                .roles(user.getRoles())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    
    /**
     * Converts a UserDTO to a User entity
     * 
     * @param dto the UserDTO to convert
     * @return the resulting User entity
     */
    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setKeycloakId(dto.getKeycloakId());
        user.setRoles(dto.getRoles());
        user.setActive(dto.isActive());
        
        return user;
    }
    
    /**
     * Creates a new User entity from a CreateUserRequest
     * 
     * @param request the create request
     * @return a new User entity
     */
    public User toEntity(CreateUserRequest request) {
        if (request == null) {
            return null;
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setKeycloakId(request.getKeycloakId());
        user.setRoles(request.getRoles());
        user.setActive(request.isActive());
        
        return user;
    }
    
    /**
     * Updates an existing User entity from an UpdateUserRequest
     * 
     * @param user the existing user to update
     * @param request the update request
     * @return the updated User entity
     */
    public User updateEntity(User user, UpdateUserRequest request) {
        if (user == null || request == null) {
            return user;
        }
        
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        
        if (request.getRoles() != null) {
            user.setRoles(request.getRoles());
        }
        
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }
        
        return user;
    }
} 