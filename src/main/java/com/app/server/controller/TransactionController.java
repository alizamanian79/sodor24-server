package com.app.server.controller;

import com.app.server.util.wallet_service_producer.WalletRMQProducer;
import com.app.server.util.wallet_service_producer.dto.request.PaymentRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    private final WalletRMQProducer walletRMQProducer;
    

//    @PostMapping("/payment")
//    public ResponseEntity<?> list(Authentication auth){
//        return ResponseEntity.status().body();
//    }


}
