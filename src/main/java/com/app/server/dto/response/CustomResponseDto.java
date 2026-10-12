package com.app.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomResponseDto<T> {
    private T data;
    private String message;
    private String details;
    private String redirect;
    private int status;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
