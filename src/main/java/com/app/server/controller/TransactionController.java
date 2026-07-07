package com.app.server.controller;

import com.app.server.util.wallet_service_producer.TransactionRMQProducer;
import com.app.server.util.wallet_service_producer.dto.request.TransactionDateRangeRequest;
import com.app.server.util.wallet_service_producer.dto.response.WalletResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    private final TransactionRMQProducer transactionRMQProducer;

    @GetMapping
    public ResponseEntity<?> list(){
        WalletResponseDto res = transactionRMQProducer.transactionsList();
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> getBySlug(@PathVariable Integer slug){
        WalletResponseDto res = transactionRMQProducer.getTransactionBySlug(slug);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PostMapping("/get-date")
    public ResponseEntity<?> getTransactionInDateRange(@RequestBody TransactionDateRangeRequest req){
        WalletResponseDto res = transactionRMQProducer.getTransactionInDateRange(req);
        return ResponseEntity.status(res.getStatus()).body(res);
    }


    @DeleteMapping("/{slug}")
    public ResponseEntity<?> deleteTransactionBySlug(@PathVariable Integer slug){
        WalletResponseDto res = transactionRMQProducer.deleteTransactionBySlug(slug);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

}
