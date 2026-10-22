package com.app.server.service;

import com.app.server.dto.request.LoginRequestDto;
import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.request.UpdateUserRequestDto;
import com.app.server.dto.request.users.ChangeAccountOtpRequestDto;
import com.app.server.dto.request.users.VerifyAccountRequestDto;
import com.app.server.dto.response.LoginResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface AuthenticationService {
    Map<String,Object> registerUser(RegisterRequestDto req);
    Map<String,Object> verifyAccountOtp(VerifyAccountRequestDto req);
    Map<String,Object> changeVerifiyAccountOtp(ChangeAccountOtpRequestDto req);

}
