package com.auth.oidcprovider.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converts Keycloak roles found in JWT tokens to Spring Security GrantedAuthority objects.
 * Handles both realm roles and client-specific roles from the Keycloak token.
 */
@Component
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String REALM_ACCESS = "realm_access";
    private static final String ROLES = "roles";
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // Extract realm roles
        Map<String, Object> realmAccess = jwt.getClaimAsMap(REALM_ACCESS);
        if (realmAccess != null && realmAccess.containsKey(ROLES)) {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) realmAccess.get(ROLES);
            authorities.addAll(roles.stream()
                    .map(roleName -> ROLE_PREFIX + roleName.toUpperCase())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()));
        }

        // Extract client roles
        Map<String, Object> resourceAccess = jwt.getClaimAsMap(RESOURCE_ACCESS);
        if (resourceAccess != null) {
            resourceAccess.forEach((clientId, clientAccess) -> {
                if (clientAccess instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> clientAccessMap = (Map<String, Object>) clientAccess;
                    if (clientAccessMap.containsKey(ROLES)) {
                        @SuppressWarnings("unchecked")
                        List<String> clientRoles = (List<String>) clientAccessMap.get(ROLES);
                        authorities.addAll(clientRoles.stream()
                                .map(roleName -> ROLE_PREFIX + clientId.toUpperCase() + "_" + roleName.toUpperCase())
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList()));
                    }
                }
            });
        }

        return authorities;
    }
} 