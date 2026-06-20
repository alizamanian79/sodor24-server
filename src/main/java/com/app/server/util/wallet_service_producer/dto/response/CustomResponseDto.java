package com.app.server.util.wallet_service_producer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomResponseDto<E,D> {
    private int status;
    private E error;
    private D data;
    private String message;
    private String details;
    private String redirect;
    private LocalDateTime timestamp;
}
