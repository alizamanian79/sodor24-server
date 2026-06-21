package com.app.server.controller;

import com.app.server.model.User;
import com.app.server.service.UserService;
import com.app.server.service.impliment.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/otp")
public class OtpController {

    private final OtpService otpService;
    private final UserService userService;

    @GetMapping("/send")
    public String sendOtp(@RequestParam String phoneNumber){
        String code = otpService.generateAndSend(phoneNumber);
        return code;
    }

    @GetMapping("/verify")
    public Object verifyOtp(@RequestParam String phoneNumber, @RequestParam String code){
        Object res = otpService.verify(phoneNumber,code);
        return res;
    }


}
