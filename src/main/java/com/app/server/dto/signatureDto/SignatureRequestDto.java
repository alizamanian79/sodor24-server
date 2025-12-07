package com.app.server.dto.signatureDto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@ToString
@AllArgsConstructor
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
    private String userId; // optional
    private Integer signatureExpired;
    private String signaturePassword;
}
