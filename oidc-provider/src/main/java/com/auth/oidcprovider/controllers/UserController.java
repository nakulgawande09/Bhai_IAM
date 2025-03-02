package com.auth.oidcprovider.controllers;

import com.auth.oidcprovider.dto.ApiResponse;
import com.auth.oidcprovider.dto.CreateUserRequest;
import com.auth.oidcprovider.dto.UpdateUserRequest;
import com.auth.oidcprovider.dto.UserDTO;
import com.auth.oidcprovider.mapper.UserMapper;
import com.auth.oidcprovider.models.User;
import com.auth.oidcprovider.services.UserService;
import com.auth.oidcprovider.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "APIs for user management")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final UserMapper userMapper;
    private final SecurityUtils securityUtils;
    
    public UserController(UserService userService, UserMapper userMapper, SecurityUtils securityUtils) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.securityUtils = securityUtils;
    }
    
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves a list of all users (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        log.debug("REST request to get all users");
        List<UserDTO> userDTOs = userService.findAllUsers().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", userDTOs));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID (protected)")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isResourceOwner(#id)")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable String id) {
        log.debug("REST request to get User with ID: {}", id);
        return userService.findUserById(id)
                .map(user -> ResponseEntity.ok(ApiResponse.success(
                        "User retrieved successfully", 
                        userMapper.toDto(user))))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + id));
    }
    
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Retrieves the currently authenticated user")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(Authentication authentication) {
        log.debug("REST request to get current user");
        
        String keycloakId = "";
        if (authentication instanceof JwtAuthenticationToken) {
            keycloakId = ((JwtAuthenticationToken) authentication).getToken().getSubject();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Not authenticated with a valid token"));
        }
        
        return userService.findByKeycloakId(keycloakId)
                .map(user -> ResponseEntity.ok(ApiResponse.success(
                        "Current user retrieved successfully", 
                        userMapper.toDto(user))))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, 
                        "User not found for the authenticated account"));
    }
    
    @PostMapping
    @Operation(summary = "Create user", description = "Creates a new user (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.debug("REST request to create User: {}", request);
        
        User newUser = userMapper.toEntity(request);
        User savedUser = userService.saveUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", userMapper.toDto(savedUser)));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates an existing user (admin or same user only)")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isResourceOwner(#id)")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable String id, 
            @Valid @RequestBody UpdateUserRequest request) {
        log.debug("REST request to update User with ID: {}", id);
        
        Optional<User> existingUserOpt = userService.findUserById(id);
        if (existingUserOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + id);
        }
        
        User existingUser = existingUserOpt.get();
        
        // Prevent changing of roles if not admin
        if (!securityUtils.isAdmin() && request.getRoles() != null) {
            request.setRoles(existingUser.getRoles());
        }
        
        User updatedUser = userMapper.updateEntity(existingUser, request);
        User savedUser = userService.saveUser(updatedUser);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", userMapper.toDto(savedUser)));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user by their ID (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        log.debug("REST request to delete User with ID: {}", id);
        
        if (!userService.findUserById(id).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + id);
        }
        
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
} 