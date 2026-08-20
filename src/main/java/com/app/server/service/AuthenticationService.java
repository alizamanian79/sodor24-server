package com.app.server.service;

import com.app.server.dto.request.LoginRequestDto;
import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.request.UpdateUserRequestDto;
import com.app.server.dto.response.LoginResponseDto;
import com.app.server.dto.response.RegisterResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface AuthenticationService {
    ResponseEntity<LoginResponseDto> login (LoginRequestDto req);
    String register(RegisterRequestDto req);
    Map<String,Object> updateUser(UpdateUserRequestDto req, String token);
}
