package com.app.server.dto.signatureMicroServiceDto;

import lombok.Data;

@Data
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
