package com.app.server.controller;

import com.app.server.dto.response.CustomResponseDto;
import com.app.server.model.OtpType;
import com.app.server.service.OtpService;
import com.app.server.service.impliment.NotificationFactory;
import com.app.server.service.impliment.OtpFactory.OtpFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OtpController {

    private final NotificationFactory notificationFactory;
    private final OtpFactory otpFactory;

    @GetMapping("/otp/generate/account/{phoneNumber}")
    public ResponseEntity<?>
    generateOtpForAccount(@PathVariable String phoneNumber){
        OtpService otpService = otpFactory.getService(OtpType.USER);
        otpService.generateOtp(phoneNumber);

       CustomResponseDto res = CustomResponseDto.builder()
                .data("کد تایید حساب به شماره شما فرستاده شد")
                .message("کد تایید حساب به شماره شما فرستاده شد")
                .details("")
                .status(200)
                .build();

        return ResponseEntity.status(res.getStatus()).body(res);

    }

    @GetMapping("/otp/verify/account/{phoneNumber}/{code}")
    public ResponseEntity<?> verifyOtpForAccount(
            @PathVariable String phoneNumber,
            @PathVariable String code) {

        OtpService otpService= otpFactory.getService(OtpType.USER);
        Object res = otpService.verifyOtp(phoneNumber,code,null);


        CustomResponseDto response = CustomResponseDto.builder()
                .data(res)
                .message("حساب شما با موفقیت تایید شد")
                .details("")
                .status(200)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);


    }



//    @GetMapping("/generate/signature/{sid}")
//    public ResponseEntity<Sodor24ResponseDto<Object>> generateOtpForSignature(
//            @PathVariable String sid) {
//        OtpService otpService= otpFactory.getService(OtpType.SIGNATURE);
//        Object res = otpService.generateOtp(sid);
//
//        return Sodor24ResponseDto.response(
//                res,
//                "کد تایید برای شما فرستاده شد",
//                "",
//                "",
//                HttpStatus.OK
//        );
//    }
//
//    @GetMapping("/verify/signature/{sid}/{code}")
//    public ResponseEntity<Sodor24ResponseDto<Object>> verifyOtpForSignature(
//            @PathVariable String sid,
//            @PathVariable String code) {
//
//        OtpService otpService= otpFactory.getService(OtpType.SIGNATURE);
//        Object res = otpService.verifyOtp(sid,code,null);
//
//        return Sodor24ResponseDto.response(
//                res,
//                "امضای شما با موفقیت با شماره تماس شما احراز هویت شد",
//                "",
//                "",
//                HttpStatus.OK
//        );
//    }


//
//
//
//    @GetMapping("/generate/account/{phoneNumber}")
//    public ResponseEntity<Sodor24ResponseDto<Object>> generateOtpForSignature(@PathVariable String phoneNumber){
//        String code =otpService.generateAndSend(phoneNumber);
//
//
//        NotificationService sendSMS = notificationFactory.getService(NotificationType.SMS);
//        sendSMS.sendNotification(phoneNumber,"به صدور24 خوش آمدید.\n" +
//                "ثبت\u200Cنام شما با موفقیت انجام شد.\n" +
//                "کد تأیید شما:" +"\s"+code+"\s"+"\n"+
//                "از همراهی شما سپاسگزاریم.");
//        return Sodor24ResponseDto.response(null,"کد تایید حساب به شماره شما فرستاده شد","","", HttpStatus.OK);
//    }




}
