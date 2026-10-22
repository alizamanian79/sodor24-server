package com.app.server.controller.authentication;

import com.app.server.annotation.VerifiedUser;
import com.app.server.dto.request.LoginRequestDto;
import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.request.users.ChangeAccountOtpRequestDto;
import com.app.server.dto.request.users.VerifyAccountRequestDto;
import com.app.server.model.User;
import com.app.server.service.AuthenticationService;
import com.app.server.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterRequestDto request
    ) {
        Map<String,Object> res = authenticationService.registerUser(request);
    return new ResponseEntity<>(res,HttpStatus.OK);
    }


    @PostMapping("/verify/otp")
    public ResponseEntity<?> verifyUserOtp(
            @Valid @RequestBody VerifyAccountRequestDto req
            ) {
        Map<String,Object> res = authenticationService.verifyAccountOtp(req);
        return new ResponseEntity<>(res,HttpStatus.OK);

    }

    @PostMapping("/change/otp")
    public ResponseEntity<?> verifyUserOtp(
            @Valid @RequestBody ChangeAccountOtpRequestDto req
    ) {
        Map<String,Object> res = authenticationService.changeVerifiyAccountOtp(req);
        return new ResponseEntity<>(res,HttpStatus.OK);

    }


    @VerifiedUser
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @Valid @RequestBody LoginRequestDto request
    ) {
        return authenticationService.login(request);
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