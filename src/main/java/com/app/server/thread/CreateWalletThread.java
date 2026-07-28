package com.app.server.thread;

import com.app.server.util.wallet_service_producer.WalletRMQProducer;
import com.app.server.util.wallet_service_producer.dto.request.CreateWalletRequestDto;
import com.app.server.util.wallet_service_producer.dto.response.WalletResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
public class CreateWalletThread implements Callable<String> {

    @Value("${application.wallet-service.currency}")
    private String currency;

    private final WalletRMQProducer walletRMQProducer;


    @Override
    public String call() throws Exception {
        return createWallet();
    }


    public String createWallet(){
        CreateWalletRequestDto req = CreateWalletRequestDto.builder()
                .sub("")
                .balance(BigDecimal.ZERO)
                .currency(currency)
                .build();
        WalletResponseDto res = walletRMQProducer.createWallet(req);
        Map<String,Object> data = (Map<String, Object>) res.getData();
        String sub = data.get("sub").toString();
        return sub;
    }
}



//private final CreateWalletThread createWallet;
//ExecutorService executor = Executors.newSingleThreadExecutor();
//Future<String> walletSubFuture = executor.submit(createWallet);