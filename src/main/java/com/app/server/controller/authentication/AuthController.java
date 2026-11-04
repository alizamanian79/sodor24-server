package com.app.server.controller.authentication;

import com.app.server.annotation.VerifiedUser;
import com.app.server.dto.request.LoginRequestDto;
import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.request.users.ChangeAccountOtpRequestDto;
import com.app.server.dto.request.users.ChangeEmailOtpRequestDto;
import com.app.server.dto.request.users.VerifyAccountRequestDto;
import com.app.server.dto.request.users.VerifyEmailRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.dto.response.LoginResponseDto;
import com.app.server.model.User;
import com.app.server.service.AuthenticationService;
import com.app.server.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<CustomResponseDto> registerUser(
            @Valid @RequestBody RegisterRequestDto request
    ) {
        CustomResponseDto res = authenticationService.registerUser(request);

        return ResponseEntity
                .status(res.getStatus())
                .body(res);
    }


    @VerifiedUser
    @PostMapping("/login")
    public LoginResponseDto loginUser(
            @Valid @RequestBody LoginRequestDto req
    ) {
        return authenticationService.login(req);
    }



    @PostMapping("/account/verify/otp")
    public ResponseEntity<?> verifyAccountOtp(
            @Valid @RequestBody VerifyAccountRequestDto req
            ) {
        Map<String,Object> res = authenticationService.verifyAccountOtp(req);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);

    }

    @PostMapping("/change/account/verify/otp")
    public ResponseEntity<CustomResponseDto> changeAccountOtp(
            @Valid @RequestBody ChangeAccountOtpRequestDto req
    ) {
        CustomResponseDto res = authenticationService.changeVerifiyAccountOtp(req);

        return ResponseEntity
                .status(res.getStatus())
                .body(res);

    }



    @PostMapping("/send/email/verify/otp")
    public ResponseEntity<?> sendEmailVerify(
            @Valid @RequestBody ChangeEmailOtpRequestDto req
    ) {
        CustomResponseDto res = authenticationService.sendOtpForEmail(req);
        return ResponseEntity
                .status(res.getStatus())
                .body(res);

    }


    @PostMapping("/email/verify/otp")
    public ResponseEntity<?> verifyEmailCode(
            @Valid @RequestBody VerifyEmailRequestDto req
    ) {
        CustomResponseDto res = authenticationService.verifyEmail(req);
        return ResponseEntity
                .status(res.getStatus())
                .body(res);

    }



//
//
//
//    @GetMapping("/me")
//    public ResponseEntity<?> getUser(Authentication authentication) {
//            Jwt jwt = (Jwt) authentication.getPrincipal();
//            User user = userService.findUserBySub(jwt.getSubject());
//        return ResponseEntity.ok(user);
//    }
}