package com.app.server.exception;

import com.github.mfathi91.time.PersianDate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalException {

    // 409 Conflict
    @ExceptionHandler(AppConflicException.class)
    public ResponseEntity<?> handleAppConflicException(AppConflicException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage() != null ? e.getMessage() : "تداخل در سیستم")
                .details(e.getDetails() != null ? e.getDetails() : "در پردازش درخواست شما تعارضی به وجود آمده است.")
                .status(HttpStatus.CONFLICT.value())
                .timestamp(PersianDate.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // ✅ 404 Not Found
    @ExceptionHandler(AppNotFoundException.class)
    public ResponseEntity<?> handleAppNotFoundException(AppNotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage() != null ? e.getMessage() : "آیتم یافت نشد")
                .details(e.getDetails() != null ? e.getDetails() : "آیتمی که به دنبال آن هستید پیدا نشد.")
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(PersianDate.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }


    // ✅ Badrequest
    @ExceptionHandler(AppBadRequestException.class)
    public ResponseEntity<?> handleAppBadRequestException(AppBadRequestException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage() != null ? e.getMessage() : "در درخواست شما مشکلی وجود دارد")
                .details(e.getDetails() != null ? e.getDetails() : "درخواست اشتباه")
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(PersianDate.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    // 401 Unauthorized
    @ExceptionHandler(AppUnAuthorizedException.class)
    public ResponseEntity<?> handleAppUnauthorizedException(AppUnAuthorizedException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage() != null ? e.getMessage() : "احراز هویت ناموفق")
                .details(e.getDetails() != null ? e.getDetails() : "")
                .status(HttpStatus.UNAUTHORIZED.value())
                .timestamp(PersianDate.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }


    // 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String messages = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining(" ، "));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(messages)
                .details("درخواست نامعتبر است.")
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(PersianDate.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


}
