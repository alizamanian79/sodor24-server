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

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        authorities.addAll(extractClientRoles(jwt));
        authorities.addAll(extractRealmRoles(jwt));

        return new JwtAuthenticationToken(
                jwt,
                authorities,
                jwt.getSubject()
        );
    }

    private Collection<SimpleGrantedAuthority> extractRealmRoles(Jwt jwt) {

        Map<String, Object> realmAccess =
                jwt.getClaimAsMap("realm_access");

        if (realmAccess == null) {
            return Collections.emptySet();
        }

        Object rolesObject = realmAccess.get("roles");

        if (!(rolesObject instanceof Collection<?> roles)) {
            return Collections.emptySet();
        }

        return roles.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    private Collection<SimpleGrantedAuthority> extractClientRoles(Jwt jwt) {

        Map<String, Object> resourceAccess =
                jwt.getClaimAsMap("resource_access");

        if (resourceAccess == null) {
            return Collections.emptySet();
        }

        Object clientObject = resourceAccess.get(clientId);

        if (!(clientObject instanceof Map<?, ?> client)) {
            return Collections.emptySet();
        }

        Object rolesObject = client.get("roles");

        if (!(rolesObject instanceof Collection<?> roles)) {
            return Collections.emptySet();
        }

        return roles.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }
}