package com.app.server.dto.request;

import com.app.server.model.Role;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class RoleChangeRequest {
    @NotEmpty(message = "نقش ها نمی توانند خالی باشند")
    private Set<Role> roles = new HashSet<>();
}
