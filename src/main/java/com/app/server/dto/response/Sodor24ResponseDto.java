package com.app.server.dto.response;

import com.github.mfathi91.time.PersianDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sodor24ResponseDto<T> {

    private T data;
    private String message;
    private String details;
    private String redirect;

    @Builder.Default
    private LocalDateTime date = LocalDateTime.now();

    private int status;

    public static <T> ResponseEntity<Sodor24ResponseDto<T>> response(
            T data,
            String message,
            String details,
            String redirect,
            HttpStatus status
    ) {

        return ResponseEntity.status(status)
                .body(
                        Sodor24ResponseDto.<T>builder()
                                .data(data)
                                .message(message)
                                .details(details)
                                .redirect(redirect)
                                .status(status.value())
                                .build()
                );
    }

    public static <T> ResponseEntity<Sodor24ResponseDto<T>> ok(
            T data,
            String message
    ) {
        return response(data, message, null, null, HttpStatus.OK);
    }

    public static <T> ResponseEntity<Sodor24ResponseDto<T>> created(
            T data,
            String message
    ) {
        return response(data, message, null, null, HttpStatus.CREATED);
    }

    public static <T> ResponseEntity<Sodor24ResponseDto<T>> badRequest(
            String message,
            String details
    ) {
        return response(null, message, details, null, HttpStatus.BAD_REQUEST);
    }

    public static <T> ResponseEntity<Sodor24ResponseDto<T>> unauthorized(
            String message
    ) {
        return response(null, message, null, null, HttpStatus.UNAUTHORIZED);
    }

    public static <T> ResponseEntity<Sodor24ResponseDto<T>> forbidden(
            String message
    ) {
        return response(null, message, null, null, HttpStatus.FORBIDDEN);
    }

    public static <T> ResponseEntity<Sodor24ResponseDto<T>> notFound(
            String message
    ) {
        return response(null, message, null, null, HttpStatus.NOT_FOUND);
    }

    public static <T> ResponseEntity<Sodor24ResponseDto<T>> conflict(
            String message
    ) {
        return response(null, message, null, null, HttpStatus.CONFLICT);
    }

    public static <T> ResponseEntity<Sodor24ResponseDto<T>> internalServerError(
            String message,
            String details
    ) {
        return response(null, message, details, null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}