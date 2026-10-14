package com.app.server.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.mfathi91.time.PersianDate;
import com.github.mfathi91.time.PersianMonth;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Data
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String message;
    private String details;
    private String redirect;
    private int status;
    private LocalDateTime timestamp;
}