package com.auth.oidcprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
    title = "OIDC Provider API",
    version = "1.0",
    description = "API Documentation for OIDC Provider"
))
public class OidcProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OidcProviderApplication.class, args);
    }
} 