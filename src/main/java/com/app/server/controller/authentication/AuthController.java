package com.app.server.controller.authentication;

import com.app.server.annotation.VerifiedUser;
import com.app.server.dto.request.LoginRequestDto;
import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.request.users.ChangeAccountOtpRequestDto;
import com.app.server.dto.request.users.VerifyAccountRequestDto;
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


    @PostMapping("/verify/otp")
    public ResponseEntity<?> verifyAccountOtp(
            @Valid @RequestBody VerifyAccountRequestDto req
            ) {
        Map<String,Object> res = authenticationService.verifyAccountOtp(req);
        return new ResponseEntity<>(res,HttpStatus.OK);

    }

    @PostMapping("/change/otp")
    public ResponseEntity<CustomResponseDto> changeAccountOtp(
            @Valid @RequestBody ChangeAccountOtpRequestDto req
    ) {
        CustomResponseDto res = authenticationService.changeVerifiyAccountOtp(req);

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