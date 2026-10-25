package com.app.server.exception;

import com.github.mfathi91.time.PersianDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.app.server.util.ExternalRequest.exception.ExternalApiException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalException {

    private static final Logger log = LoggerFactory.getLogger(GlobalException.class);

    // ---------- helper ----------
    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, String details, String redirect) {
        ErrorResponse.ErrorResponseBuilder builder = ErrorResponse.builder()
                .message(message)
                .details(details)
                .status(status.value())
                .timestamp(LocalDateTime.now()); // String تا مشکل سریالایز نداشته باشیم

        if (redirect != null) {
            builder.redirect(redirect);
        }

        return ResponseEntity.status(status).body(builder.build());
    }

    private String orDefault(String value, String defaultValue) {
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }

    // ---------- External API ----------
    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<?> handleExternalApiException(ExternalApiException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ex.getMessage());
    }

    // ---------- 409 Conflict ----------
    @ExceptionHandler(AppConflicException.class)
    public ResponseEntity<ErrorResponse> handleAppConflicException(AppConflicException e) {
        return build(
                HttpStatus.CONFLICT,
                orDefault(e.getMessage(), "تداخل در سیستم"),
                orDefault(e.getDetails(), "در پردازش درخواست شما تعارضی به وجود آمده است."),
                null
        );
    }

    // ---------- 403 Forbidden ----------
    @ExceptionHandler(AppForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleAppForbiddenException(AppForbiddenException e) {
        return build(
                HttpStatus.FORBIDDEN,
                orDefault(e.getMessage(), "دسترسی غیرمجاز"),
                orDefault(e.getDetails(), "دسترسی غیر مجاز"),
                e.getRedirect() != null ? e.getRedirect() : ""
        );
    }



    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            org.springframework.security.access.AccessDeniedException e) {
        return build(
                HttpStatus.FORBIDDEN,
                "دسترسی به این منبع غیرمجاز است",
                "با ادمین تماس گرفته و درخواست تغییر نقش دهید",
                null
        );
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(
            org.springframework.security.core.AuthenticationException e) {
        return build(HttpStatus.UNAUTHORIZED, "احراز هویت ناموفق", "", null);
    }

    // ---------- 500 Internal ----------
    @ExceptionHandler(AppInternalException.class)
    public ResponseEntity<ErrorResponse> handleAppInternalException(AppInternalException e) {
        log.error("AppInternalException", e);
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                orDefault(e.getMessage(), "خطای سرور"),
                orDefault(e.getDetails(), "خطای سرور"),
                null
        );
    }

    // ---------- 404 Not Found ----------
    @ExceptionHandler(AppNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAppNotFoundException(AppNotFoundException e) {
        return build(
                HttpStatus.NOT_FOUND,
                orDefault(e.getMessage(), "آیتم یافت نشد"),
                orDefault(e.getDetails(), "آیتمی که به دنبال آن هستید پیدا نشد."),
                null
        );
    }

    // ---------- 400 Bad Request ----------
    @ExceptionHandler(AppBadRequestException.class)
    public ResponseEntity<ErrorResponse> handleAppBadRequestException(AppBadRequestException e) {
        return build(
                HttpStatus.BAD_REQUEST,
                orDefault(e.getMessage(), "در درخواست شما مشکلی وجود دارد"),
                orDefault(e.getDetails(), "درخواست اشتباه"),
                null
        );
    }

    // ---------- 401 Unauthorized ----------
    @ExceptionHandler(AppUnAuthorizedException.class)
    public ResponseEntity<ErrorResponse> handleAppUnauthorizedException(AppUnAuthorizedException e) {
        return build(
                HttpStatus.UNAUTHORIZED,
                orDefault(e.getMessage(), "احراز هویت ناموفق"),
                orDefault(e.getDetails(), ""),
                null
        );
    }

    // ---------- 400 Validation ----------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String messages = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining(" ، "));

        return build(
                HttpStatus.BAD_REQUEST,
                messages,
                "درخواست نامعتبر است.",
                null
        );
    }

    // ---------- Fallback (همیشه آخر) ----------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception e) {
        log.error("Unhandled exception", e);
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "خطای سرور",
                "خطای پیش‌بینی‌نشده",
                null
        );
    }
}