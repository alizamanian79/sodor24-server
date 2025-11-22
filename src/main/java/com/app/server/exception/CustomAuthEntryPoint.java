package com.app.server.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    @Value("${app.client.login.redirect-url}")
    private String redirectUrl;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // فقط 401 برگردون
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = "{\"message\":\"Unauthorized\"}";
        response.getWriter().write(json);
        response.getWriter().flush();
    }

}
