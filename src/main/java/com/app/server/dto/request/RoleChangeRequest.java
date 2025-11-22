package com.app.server.dto.request;

import com.app.server.model.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class RoleChangeRequest {
    private Set<Role> roles = new HashSet<>();
}
