package com.app.server.controller;

import com.app.server.util.wallet_service_producer.WalletRMQProducer;
import com.app.server.util.wallet_service_producer.dto.request.PaymentRequestDto;
import com.app.server.util.wallet_service_producer.dto.response.WalletResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class TransactionController {

    private final WalletRMQProducer walletRMQProducer;
    

    @PostMapping("/request")
    public ResponseEntity<?> paymentRequest(@RequestBody PaymentRequestDto req){
        WalletResponseDto res = walletRMQProducer.paymentRequest(req);
        return ResponseEntity.status(res.getStatus()).body(res);
    }


}
