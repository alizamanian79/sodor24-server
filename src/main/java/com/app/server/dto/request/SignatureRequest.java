package com.app.server.dto.request;

import lombok.Data;

@Data
public class SignatureRequest {

    private Long userId;
    private Long signatureId;
    private String country;
    private String  reason;
    private String location;
    private String organization;
    private String department;
    private String state;
    private String city;
    private String email;
    private String title;
    private String signaturePassword;
}
