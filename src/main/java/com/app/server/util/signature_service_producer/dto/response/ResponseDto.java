package com.app.server.util.signature_service_producer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseDto<D> {

    private D data;
    private String message;
    private String details;
    private int status;
    private String redirect;

    private LocalDateTime timestamp = LocalDateTime.now();
}
