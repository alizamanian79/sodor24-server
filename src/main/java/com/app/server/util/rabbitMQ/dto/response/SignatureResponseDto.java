package com.app.server.util.rabbitMQ.dto.response;

import lombok.*;
import org.springframework.http.HttpStatus;
import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignatureResponseDto {
    private String message;
    private String cert;
    private String p12;
    private String privateKey;
    private String publicKey;
    private Date timestamp;
    private String username;
    private HttpStatus status;
    private String userId;

}
