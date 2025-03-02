package com.auth.oidcprovider.config;

import com.auth.oidcprovider.services.UserService;
import com.auth.oidcprovider.util.JwtUtil;
import com.auth.oidcprovider.util.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityBeansConfig {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    public SecurityBeansConfig(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    
    @Bean
    public SecurityUtils securityUtils() {
        return new SecurityUtils(userService, jwtUtil);
    }
} 