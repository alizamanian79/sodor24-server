package com.app.server.util.signature_service_producer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SignatureServiceResponseDto<D> {
    private int status;
    private D data;
    private String message;
    private String details;
    private String redirect;
    private LocalDateTime timestamp;

    public LocalDateTime getTimestamp() {
        return LocalDateTime.now();
    }
}
