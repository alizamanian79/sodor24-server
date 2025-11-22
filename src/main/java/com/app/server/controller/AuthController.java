package com.app.server.controller;

import com.app.server.dto.request.LoginRequestDto;
import com.app.server.dto.request.RegisterRequestDto;
import com.app.server.dto.response.LoginResponseDto;
import com.app.server.dto.response.RegisterResponseDto;
import com.app.server.exception.AppUnAuthorizedException;
import com.app.server.model.User;
import com.app.server.service.UserService;
import com.app.server.service.JwtService;
import com.github.mfathi91.time.PersianDate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> registerUser(@Valid @RequestBody RegisterRequestDto req) {
        RegisterResponseDto user = userService.registerUser(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequestDto req) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );

            if (!authentication.isAuthenticated()) {
                throw new AppUnAuthorizedException("احراز هویت انجام نشد");
            }

            String accessToken = jwtService.generateAccessToken(req.getUsername(), authentication.getAuthorities());
            String refreshToken = req.isRefresh_token()
                    ? jwtService.generateRefreshToken(req.getUsername(), authentication.getAuthorities())
                    : "";

            LoginResponseDto response = LoginResponseDto.builder()
                    .message("ورود با موفقیت انجام شد")
                    .access_token(accessToken)
                    .refresh_token(refreshToken)
                    .timestamp(PersianDate.now())
                    .build();



            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            throw new AppUnAuthorizedException("نام کاربری یا رمز عبور اشتباه است");
        } catch (AuthenticationException e) {
            throw new AppUnAuthorizedException("احراز هویت ناموفق بود");
        }
    }


    @GetMapping("/me")
    public ResponseEntity<?> getUser(Authentication authentication) {
        try {
            Object auth = authentication.getPrincipal();
            return new ResponseEntity<>(auth, HttpStatus.OK);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

}
