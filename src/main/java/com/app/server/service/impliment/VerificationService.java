//package com.app.server.service.impliment;
//
//import com.app.server.dto.response.CustomResponseDto;
//import com.app.server.exception.AppNotFoundException;
//import com.app.server.model.Otp;
//import com.app.server.model.User;
//import com.app.server.service.SignatureService;
//import com.app.server.service.UserService;
//import com.app.server.thread.CreateWalletThread;
//import com.app.server.thread.SendSMSThread;
//import com.app.server.util.wallet_service_producer.dto.request.ActivityRequestDto;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class VerificationService {
//
//    private final SignatureService signatureService;
//    private final SendSMSThread smsThread;
//
//
//
//    public CustomResponseDto verifyAccountOtp(String otp) {
//        return signatureService.verifySignature(otp);
//    }
//
//    public CustomResponseDto verifySignatureOtp(String otp) {
//       return signatureService.verifySignature(otp);
//    }
//}