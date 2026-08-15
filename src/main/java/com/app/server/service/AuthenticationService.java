package com.app.server.service;

import com.app.server.dto.request.LoginRequestDto;
import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.response.LoginResponseDto;
import com.app.server.dto.response.RegisterResponseDto;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity<LoginResponseDto> login (LoginRequestDto req);
    String register(RegisterRequestDto req);
}
