package com.app.server.controller;

import com.app.server.dto.response.Sodor24ResponseDto;
import com.app.server.model.NotificationType;
import com.app.server.service.NotificationService;
import com.app.server.service.impliment.NotificationFactory;
import com.app.server.service.impliment.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/otp")
public class VerifiyAccountController {

    private final OtpService otpService;
    private final NotificationFactory notificationFactory;

    @GetMapping("/generate/account/{phoneNumber}")
    public ResponseEntity<Sodor24ResponseDto<Object>> generateOtpForAccount(@PathVariable String phoneNumber){
        String code =otpService.generateAndSend(phoneNumber);
        NotificationService sendSMS = notificationFactory.getService(NotificationType.SMS);
        sendSMS.sendNotification(phoneNumber,"به صدور24 خوش آمدید.\n" +
                "ثبت\u200Cنام شما با موفقیت انجام شد.\n" +
                "کد تأیید شما:" +"\s"+code+"\s"+"\n"+
                "از همراهی شما سپاسگزاریم.");
        return Sodor24ResponseDto.response(null,"کد تایید حساب به شماره شما فرستاده شد","","", HttpStatus.OK);
    }

    @GetMapping("/verify/account/{phoneNumber}/{code}")
    public ResponseEntity<Sodor24ResponseDto<Object>> verifyOtpForAccount(
            @PathVariable String phoneNumber,
            @PathVariable String code) {

        boolean res = otpService.verify(phoneNumber, code);
        return Sodor24ResponseDto.response(
                res,
                "حساب شما با موفقیت تایید شد",
                "",
                "",
                HttpStatus.OK
        );
    }

}
