//package com.app.server.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/otp")
//public class OtpController {
//
//    private final OtpService otpService;
//
//    @GetMapping("/generate")
//    public String sendOtp(){
//        String code = otpService.generate(0,2L);
//        return code;
//    }
//
//
////    @GetMapping("/verify")
////    public Object verifyOtp(@RequestParam String phoneNumber, @RequestParam String code){
////        Object res = otpService.verify(phoneNumber,code);
////        return res;
////    }
//
//
//}
