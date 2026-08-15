package com.app.server.service.impliment;

import com.app.server.dto.request.LoginRequestDto;
import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.response.LoginResponseDto;
import com.app.server.dto.response.RegisterResponseDto;
import com.app.server.exception.AppUnAuthorizedException;
import com.app.server.service.AuthenticationService;
import com.app.server.util.ExternalRequest.ExternalRequest;
import com.app.server.util.ExternalRequest.dto.ExternalRequestDto;
import com.app.server.util.ExternalRequest.dto.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {


    private final ExternalRequest externalRequest;

    @Override
    public ResponseEntity<LoginResponseDto> login(LoginRequestDto req) {

        LoginResponseDto res = LoginResponseDto.builder()
                .access_token("")
                .refresh_token("")
                .message("نام کاربری یا رمز عبور اشتباه میباشد")
                .status(HttpStatus.UNAUTHORIZED)
                .build();

        try {
            Map<String, Object> result = externalRequest.sendRequest(
                    ExternalRequestDto.builder()
                            .url("http://localhost:8081/api/v1/authentication/direct/login")
                            .method(Method.POST)
                            .body(Map.of(
                                    "username", req.getUsername(),
                                    "password", req.getPassword()
                            ))
                            .build()
            );


            int status = (int) result.getOrDefault("status", 200);
            res.setMessage("ورود با موفقیت انجام شد");
            res.setAccess_token(result.get("access_token").toString());
            res.setRefresh_token(result.get("refresh_token").toString());
            res.setStatus(HttpStatus.OK);

            return ResponseEntity
                    .status(status)
                    .body(res);




        } catch (Exception e) {
            throw new AppUnAuthorizedException(res.getMessage());
        }

    }

    @Override
    public ResponseEntity<RegisterResponseDto> register(RegisterRequestDto req) {

        RegisterResponseDto res = RegisterResponseDto.builder()
                .message("ثبت نام شما با موفقیت انجام شد . لطفا کد ارسال شده را وارد نمایید")
                .status(200)
                .timestamp(Date.from(Instant.now()))
                .details("")
                .build();

        Map<String, Object> result = externalRequest.sendRequest(
                ExternalRequestDto.builder()
                        .url("http://localhost:8081/api/v1/authentication/direct/register")
                        .method(Method.POST)
                        .body(Map.of(
                                "username", req.getUsername(),
                                "email", req.getEmail(),
                                "password", req.getPassword(),
                                "firstName", req.getFirstName(),
                                "lastName", req.getLastName(),
                                "phoneNumber", req.getPhoneNumber(),
                                "nationalCode", req.getNationalCode()
                        ))
                        .build()
        );


        result.get("sub").toString();
        int status = (int) result.getOrDefault("status", 200);
        return ResponseEntity
                .status(status)
                .body(res);
    }


}
