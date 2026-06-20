package com.app.server.event;


import com.app.server.util.wallet_service_producer.WalletRMQProducer;
import com.app.server.util.wallet_service_producer.dto.request.CreateWalletRequestDto;
import com.app.server.util.wallet_service_producer.dto.response.CustomResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InitialEvent implements Runnable{


    private final WalletRMQProducer walletRMQProducer;



    public void run() {
        String walletId = createWallet();
        System.out.println(walletId);

    }

    public String createWallet() {
        CreateWalletRequestDto request =
                CreateWalletRequestDto.builder()
                        .sub("")
                        .balance(BigDecimal.ZERO)
                        .build();

        CustomResponseDto res =
                walletRMQProducer.createWallet(request);

        Map<String,Object> data =
                (Map<String,Object>) res.getData();

        return data.get("sub").toString();
    }
}