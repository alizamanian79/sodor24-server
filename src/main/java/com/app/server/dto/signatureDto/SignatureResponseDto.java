package com.app.server.dto.signatureDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignatureResponseDto {
    private String message;
    private String cert;
    private String privateKey;
    private String publicKey;
    private String timestamp;
    private String username;
    private String status;
    private String userId;
}
