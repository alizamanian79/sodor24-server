package com.app.server.service.impliment;

import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.request.users.ChangeAccountOtpRequestDto;
import com.app.server.dto.request.users.VerifyAccountRequestDto;
import com.app.server.repository.UserRepository;
import com.app.server.service.AuthenticationService;
import com.app.server.util.ExternalRequest.ExternalRequest;
import com.app.server.util.ExternalRequest.dto.ExternalRequestDto;
import com.app.server.util.ExternalRequest.dto.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {


    private final ExternalRequest externalRequest;


    @Override
    public Map<String, Object> registerUser(RegisterRequestDto req) {
        ExternalRequestDto request = ExternalRequestDto.builder()
                .url("http://localhost:8081/api/v1/authentication/direct/register")
                .body(req)
                .token(null)
                .method(Method.POST)
                .param(null)
                .build();
        Map<String,Object> res = externalRequest.sendRequest(request);

        return res;
    }


    @Override
    public Map<String,Object> verifyAccountOtp(VerifyAccountRequestDto req){
        ExternalRequestDto request = ExternalRequestDto.builder()
                .url("http://localhost:8081/api/v1/authentication/verify/otp")
                .body(req)
                .token(null)
                .method(Method.POST)
                .param(null)
                .build();
        Map<String,Object> res = externalRequest.sendRequest(request);
        return res;
    }


    @Override
    public Map<String,Object> changeVerifiyAccountOtp(ChangeAccountOtpRequestDto req){

        ExternalRequestDto request = ExternalRequestDto.builder()
                .url("http://localhost:8081/api/v1/authentication/change/otp")
                .body(req)
                .token(null)
                .method(Method.POST)
                .param(null)
                .build();
        Map<String,Object> res = externalRequest.sendRequest(request);

        return res;
    }





}
