package com.app.server.controller;

import com.app.server.annotation.VerifiedUser;
import com.app.server.dto.request.LoginRequestDto;
import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.response.LoginResponseDto;
import com.app.server.dto.response.RegisterResponseDto;
import com.app.server.exception.AppUnAuthorizedException;
import com.app.server.model.User;
import com.app.server.service.AuthenticationService;
import com.app.server.service.UserService;
import com.app.server.util.ExternalRequest.ExternalRequest;
import com.app.server.util.ExternalRequest.dto.ExternalRequestDto;
import com.app.server.util.ExternalRequest.dto.Method;
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
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterRequestDto request
    ) {
        // register in keycloak
        String sub = authenticationService.register(request);

        if (!sub.isBlank()){
            request.setSub(sub);
            RegisterResponseDto res =userService.registerUser(request);
            return new ResponseEntity<>(res,HttpStatus.OK);
        }

    return new ResponseEntity<>("",HttpStatus.OK);

    }


    @VerifiedUser
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @Valid @RequestBody LoginRequestDto request
    ) {
        return authenticationService.login(request);
    }








    @GetMapping("/me")
    public ResponseEntity<?> getUser(Authentication authentication) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            User user = userService.findUserBySub(jwt.getSubject());
        return ResponseEntity.ok(user);
    }
}