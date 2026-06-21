package com.app.server.controller;

import com.app.server.util.wallet_service_producer.WalletRMQProducer;
import com.app.server.util.wallet_service_producer.dto.response.WalletResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final WalletRMQProducer walletRMQProducer;



    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> list(){
        WalletResponseDto res = walletRMQProducer.walletLists();
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PreAuthorize("hasRole('ADMIN') or #sub == authentication.principal.walletId")
    @GetMapping("/{sub}")
    public ResponseEntity<?> getBySub(@PathVariable String sub){
        WalletResponseDto res = walletRMQProducer.getWalletBySub(sub);
        return ResponseEntity.status(res.getStatus()).body(res);
    }



}
