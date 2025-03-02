package com.auth.oidcprovider.config;

import com.auth.oidcprovider.util.JwtTestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Test security configuration that replaces the actual security configuration for testing.
 */
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    @Autowired
    private JwtTestUtil jwtTestUtil;

    /**
     * Creates a test security filter chain.
     *
     * @param http the HTTP security
     * @return the security filter chain
     * @throws Exception if an error occurs
     */
    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                // Allow registration endpoint without authentication
                .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/refresh").permitAll()
                
                // Admin access
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                
                // Product endpoint permissions
                .requestMatchers("/api/products/all").authenticated()
                .requestMatchers("/api/products/create").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/products/add-role/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/products/remove-role/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/products/accessible").authenticated()
                
                // Require authentication for all other API endpoints
                .requestMatchers("/api/**").authenticated()
                
                // Allow public access to docs and error pages
                .requestMatchers("/docs/**", "/error").permitAll()
                
                // All other requests need authentication
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(testJwtDecoder()))
            );
            
        return http.build();
    }

    /**
     * Creates a JWT decoder for testing.
     *
     * @return the JWT decoder
     */
    @Bean
    @Primary
    public JwtDecoder testJwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(jwtTestUtil.getSigningKey()).build();
    }
} 