package com.app.server.controller;

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
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final WalletRMQProducer walletRMQProducer;


    @PostMapping("/request")
    public ResponseEntity<?> paymentRequest(
            @RequestBody PaymentRequestDto req,
            Authentication auth,
            HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        String token = bearerToken.substring(7);

        req.setCallbackUrl("http://localhost:8181/api/v1/payment/callback?sub="
                + req.getSub()
                + "&amount=" + req.getAmount()
                +"&gateway="+req.getPaymentServiceName()
                + "&token=" + token);

        WalletResponseDto res = walletRMQProducer.paymentRequest(req);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/callback")
    public ResponseEntity<?> paymentRequest(@RequestParam String sub,
                                            @RequestParam BigDecimal amount,
                                            @RequestParam String Authority,
                                            @RequestParam String gateway,
                                            @RequestParam String Status){

        Map<String,Object> dt = new HashMap<String,Object>();
        dt.put("authority",Authority);

        PaymentVerifierRequestDto req = PaymentVerifierRequestDto.builder()
                .sub(sub)
                .amount(amount)
                .description("")
                .callBackUrl("")
                .paymentServiceName(gateway)
                .data(dt)
                .build();
        WalletResponseDto res = walletRMQProducer.paymentVerifier(req);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

}
