package com.app.server.controller;

import com.app.server.exception.AppBadRequestException;
import com.app.server.service.SignatureService;
import com.app.server.util.wallet_service_producer.WalletRMQProducer;
import com.app.server.util.wallet_service_producer.dto.request.PaymentRequestDto;
import com.app.server.util.wallet_service_producer.dto.request.PaymentVerifierRequestDto;
import com.app.server.util.wallet_service_producer.dto.response.WalletResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> verifyRequest(
            @RequestParam String sub,
            @RequestParam Integer slug,
            @RequestParam String gateway,
            @RequestParam(required = false, defaultValue = "") String Authority,
            @RequestParam(required = false, defaultValue = "") String Status) {




        System.out.println("sub =>\s"+ sub);
        System.out.println("slug =>\s"+ slug);
        System.out.println("gateway =>\s"+ gateway);
        System.out.println("Status =>\s"+ Status);
        System.out.println("Authority => \s" + Authority);


        Map<String, Object> object = new HashMap<String,Object>();
        object.put("authority", Authority);


        PaymentVerifierRequestDto req = PaymentVerifierRequestDto.builder()
                .sub(sub)
                .slug(slug)
                .gateway(gateway)
                .callbackUrl("")
                .data(object)
                .build();

        WalletResponseDto res = walletRMQProducer.paymentVerifier(req);

        if (res == null) {
            return null;
        }

        return ResponseEntity.status(res.getStatus()).body(res);
    }

//    @GetMapping("/callback")
//    public ResponseEntity<?> verifyRequest() {
//        return new ResponseEntity<>("salam", HttpStatus.OK);
//    }

}
