package com.app.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterResponseDto {
    private String message;
    private String details;
    private Date timestamp;
    private int status;
}
