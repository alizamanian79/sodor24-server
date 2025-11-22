package com.app.server.exception;

import com.github.mfathi91.time.PersianDate;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
        response.setContentType("application/json;charset=UTF-8"); // مهم: UTF-8


        ErrorResponse responseBody = ErrorResponse.builder()
                .message("دسترسی به این منبع غیرمجاز است")
                .details("با ادمین تماس گرفته و درخواست تغییر نقش دهید")
                .timestamp(PersianDate.now())
                .status(403)
                .build();


        response.getWriter().write(new String(
                objectMapper.writeValueAsBytes(responseBody),
                "UTF-8"
        ));
    }

}
