package com.app.server.dto.response.user;

import lombok.Data;

@Data
public class AccountDataDto {

    private String email;
    private String firstName;
    private Long id;
    private Boolean inDatabase;
    private Boolean inKeycloak;
    private KeycloakDataDto keycloak;
    private String lastName;
    private String nationalCode;
    private String phoneNumber;
    private String profileImageContentType;
    private String sub;
    private String username;
}
