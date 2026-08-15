package com.app.server.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class JwtKeycloakConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Value("${application.keycloak.client.id}")
    private String clientId;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        Collection<SimpleGrantedAuthority> authorities = new HashSet<>();

        // Client Roles
        authorities.addAll(extractClientRoles(jwt));

        // Realm Roles (اختیاری)
        authorities.addAll(extractRealmRoles(jwt));

        return new JwtAuthenticationToken(
                jwt,
                authorities,
                getPrincipalName(jwt)
        );
    }

    private String getPrincipalName(Jwt jwt) {

        String username = jwt.getClaimAsString("preferred_username");

        if (username != null) {
            return username;
        }

        return jwt.getSubject();
    }

    private Collection<SimpleGrantedAuthority> extractRealmRoles(Jwt jwt) {

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess == null || realmAccess.isEmpty()) {
            return Collections.emptyList();
        }

        Collection<String> roles =
                (Collection<String>) realmAccess.get("roles");

        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    private Collection<SimpleGrantedAuthority> extractClientRoles(Jwt jwt) {

        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess == null || resourceAccess.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Object> client =
                (Map<String, Object>) resourceAccess.get(clientId);

        if (client == null || client.isEmpty()) {
            return Collections.emptyList();
        }

        Collection<String> roles =
                (Collection<String>) client.get("roles");

        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }
}