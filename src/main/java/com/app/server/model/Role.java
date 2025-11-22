package com.app.server.model;

import java.util.Set;

public enum Role {

    USER(Set.of(Authority.READ)),
    ADMIN(Set.of(Authority.READ, Authority.WRITE, Authority.EDIT, Authority.DELETE));

    private final Set<Authority> authorities;

    Role(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }
}
