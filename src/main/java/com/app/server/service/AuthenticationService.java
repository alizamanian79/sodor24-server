package com.app.server.service;

import com.app.server.dto.request.LoginRequestDto;
import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.request.UpdateUserRequestDto;
import com.app.server.dto.request.users.ChangeAccountOtpRequestDto;
import com.app.server.dto.request.users.ChangeEmailOtpRequestDto;
import com.app.server.dto.request.users.VerifyAccountRequestDto;
import com.app.server.dto.request.users.VerifyEmailRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.dto.response.LoginResponseDto;
import com.app.server.exception.AppUnAuthorizedException;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface AuthenticationService {
    CustomResponseDto registerUser(RegisterRequestDto req);
    LoginResponseDto login(LoginRequestDto req) throws AppUnAuthorizedException;


    Map<String,Object> verifyAccountOtp(VerifyAccountRequestDto req);
    CustomResponseDto changeVerifiyAccountOtp(ChangeAccountOtpRequestDto req);


    CustomResponseDto  sendOtpForEmail(ChangeEmailOtpRequestDto req);
    CustomResponseDto verifyEmail(VerifyEmailRequestDto req);
}
