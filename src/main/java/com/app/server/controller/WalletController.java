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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

//    @PreAuthorize("hasRole('ADMIN') or #sub == authentication.principal.walletId")
    @GetMapping("/{sub}")
    public ResponseEntity<?> getBySub(@PathVariable String sub){
        WalletResponseDto res = walletRMQProducer.getWalletBySub(sub);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

//    @PreAuthorize("hasRole('ADMIN') or #sub == authentication.principal.walletId")
    @GetMapping("/{sub}/{slug}")
    public ResponseEntity<?> getBySub(
            @PathVariable String sub,
            @PathVariable String slug
    ){

        WalletResponseDto res = walletRMQProducer.getWalletBySub(sub);

        Map<String,Object> data = (Map<String,Object>) res.getData();

        List<Map<String,Object>> transactions =
                (List<Map<String,Object>>) data.get("transactions");


        Map<String,Object> transaction = transactions.stream()
                .filter(t -> slug.equals(String.valueOf(t.get("slug"))))
                .findFirst()
                .orElse(null);


        if(transaction == null){
            return ResponseEntity.status(404).body(
                    Map.of(
                            "message","تراکنش پیدا نشد"
                    )
            );
        }


        return ResponseEntity.status(200).body(
                Map.of(
                        "status",200,
                        "data",transaction,
                        "message","اطلاعات تراکنش دریافت شد"
                )
        );
    }

}
