package com.app.server.dto.response.user;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class KeycloakDataDto {

    private Map<String, List<String>> clientRoles;
    private Long createdTimestamp;
    private String email;
    private Boolean emailVerified;
    private Boolean enabled;
    private String firstName;
    private String lastName;
    private List<String> realmRoles;
    private String username;
}
