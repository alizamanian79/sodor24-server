package com.app.server.service.impliment;

import com.app.server.dto.request.LoginRequestDto;
import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.request.users.ChangeAccountOtpRequestDto;
import com.app.server.dto.request.users.VerifyAccountRequestDto;
import com.app.server.dto.response.LoginResponseDto;
import com.app.server.exception.AppUnAuthorizedException;
import com.app.server.model.User;
import com.app.server.repository.UserRepository;
import com.app.server.service.AuthenticationService;
import com.app.server.service.UserService;
import com.app.server.util.ExternalRequest.ExternalRequest;
import com.app.server.util.ExternalRequest.dto.ExternalRequestDto;
import com.app.server.util.ExternalRequest.dto.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {


    private final UserService userService;
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


    public LoginResponseDto login(LoginRequestDto req) throws AppUnAuthorizedException{
        try{
            ExternalRequestDto request = ExternalRequestDto.builder()
                    .url("http://localhost:8081/api/v1/authentication/direct/login")
                    .body(req)
                    .token(null)
                    .method(Method.POST)
                    .param(null)
                    .build();
            Map<String,Object> res = externalRequest.sendRequest(request);

            return LoginResponseDto.builder()
                    .message("ورود موفقیت شما با موفقیت تایید شد")
                    .status(HttpStatus.OK)
                    .access_token(res.get("access_token").toString())
                    .refresh_token(res.get("refresh_token").toString())
                    .build();

        } catch (RuntimeException e) {
            throw new AppUnAuthorizedException("نام کاربری یا کلمه عبور اشتباه میباشد");
        }


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

        String sub = res.get("sub").toString();
        if (!sub.isEmpty()){
            accountVerifiedAndDo(sub);
        }
        return res;
    }


    public void accountVerifiedAndDo(String sub){

        RegisterRequestDto.builder()
                        .email();
        userService.createUser();

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



//    public void afterAccountVerifiedDo(Map<String,Object> res){
//      Map<String,Object> data =  res.get("data");
//      data.get("sub");
//    }


}
