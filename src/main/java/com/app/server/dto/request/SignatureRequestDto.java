package com.app.server.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignatureRequestDto {
    private String username;
    private String country;
    private String reason;
    private String location;
    private String organization;
    private String department;
    private String state;
    private String city;
    private String email;
    private String title;
    private String signatureExpired;
    private String signaturePassword;

    private int usageCount;
    private boolean valid;

}
