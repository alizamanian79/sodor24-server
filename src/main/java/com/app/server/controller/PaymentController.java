package com.app.server.controller;

import com.app.server.exception.AppBadRequestException;
import com.app.server.service.SignatureService;
import com.app.server.util.wallet_service_producer.WalletRMQProducer;
import com.app.server.util.wallet_service_producer.dto.request.PaymentRequestDto;
import com.app.server.util.wallet_service_producer.dto.request.PaymentVerifierRequestDto;
import com.app.server.util.wallet_service_producer.dto.response.WalletResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final WalletRMQProducer walletRMQProducer;
    private final SignatureService signatureService;


    @PostMapping("/request")
    public ResponseEntity<?> paymentRequest(
            @RequestBody PaymentRequestDto req,
            Authentication auth,
            HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");
        String token = bearerToken.substring(7);
        req.setCallbackUrl(req.getCallbackUrl());

        WalletResponseDto res = walletRMQProducer.paymentRequest(req);
        return ResponseEntity.status(res.getStatus()).body(res);
    }


    @GetMapping("/callback")
    public ResponseEntity<?> verifyRequest(@RequestParam String sub,
                                           @RequestParam BigDecimal amount,
                                           @RequestParam String paymentMethod,
                                           @RequestParam String Authority,
                                           @RequestParam String Status,
                                           @RequestParam(defaultValue = "",required = false) String description
    ){

        if (!Status.equals("OK")){
            throw new AppBadRequestException("تراکنش ناموفق بود");
        }

        Map<String,Object> dt = new HashMap<>();
        dt.put("authority",Authority);


        System.out.println("description is \s =>");
        System.out.println(description);


        PaymentVerifierRequestDto req = PaymentVerifierRequestDto.builder()
                .sub(sub)
                .amount(amount)
                .description(description)
                .callBackUrl("")
                .paymentServiceName(paymentMethod)
                .data(dt)
                .build();
        WalletResponseDto res = walletRMQProducer.paymentVerifier(req);


        return ResponseEntity.status(res.getStatus()).body("تراکنش با موفقیت انجام شد");
    }

}
