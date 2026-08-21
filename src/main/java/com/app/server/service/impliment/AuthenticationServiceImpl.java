package com.app.server.service.impliment;

import com.app.server.dto.request.LoginRequestDto;
import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.request.UpdateUserRequestDto;
import com.app.server.dto.response.LoginResponseDto;
import com.app.server.dto.response.RegisterResponseDto;
import com.app.server.exception.AppUnAuthorizedException;
import com.app.server.service.AuthenticationService;
import com.app.server.service.UserService;
import com.app.server.util.ExternalRequest.ExternalRequest;
import com.app.server.util.ExternalRequest.dto.ExternalRequestDto;
import com.app.server.util.ExternalRequest.dto.Method;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${application.authentication.service}")
    private String authServiceUrl;

    private final ExternalRequest externalRequest;
    private final UserService userService;


    @Override
    public ResponseEntity<LoginResponseDto> login(LoginRequestDto req) {

        userService.findUserByUsername(req.getUsername());

        LoginResponseDto res = LoginResponseDto.builder()
                .access_token("")
                .refresh_token("")
                .message("نام کاربری یا رمز عبور اشتباه میباشد")
                .status(HttpStatus.UNAUTHORIZED)
                .build();

        try {
            Map<String, Object> result = externalRequest.sendRequest(
                    ExternalRequestDto.builder()
                            .url(authServiceUrl+"/api/v1/authentication/direct/login")
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
    public String register(RegisterRequestDto req) {

        RegisterResponseDto res = RegisterResponseDto.builder()
                .message("ثبت نام شما با موفقیت انجام شد . لطفا کد ارسال شده را وارد نمایید")
                .status(200)
                .timestamp(Date.from(Instant.now()))
                .details("")
                .build();

        Map<String, Object> result = externalRequest.sendRequest(
                ExternalRequestDto.builder()
                        .url(authServiceUrl+"/api/v1/authentication/direct/register")
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


        String sub = result.get("sub").toString();
        return sub;
    }

    @Override
    public Map<String,Object> updateUser(UpdateUserRequestDto req,String token) {

        Map<String,Object> res= new HashMap<String,Object>();

        ExternalRequestDto request = ExternalRequestDto.builder()
                .url(authServiceUrl+"/api/v1/user")
                .method(Method.PUT)
                .token(token)
                .body(Map.of(

                        "sub",req.getSub(),
                        "username", req.getUsername(),
                        "email", req.getEmail(),
                        "password", req.getPassword(),
                        "firstName", req.getFirstName(),
                        "lastName", req.getLastName(),
                        "phoneNumber", req.getPhoneNumber(),
                        "nationalCode",req.getNationalCode()
                ))
                .build();

       Map<String,Object> response = externalRequest.sendRequest(request);
        res.put("message",response.get("message"));
        res.put("access_token",response.get("access_token"));
        res.put("refresh_token",response.get("refresh_token"));
      return res;

    }





}
