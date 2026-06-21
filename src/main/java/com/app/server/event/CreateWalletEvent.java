package com.app.server.event;

import com.app.server.util.wallet_service_producer.WalletRMQProducer;
import com.app.server.util.wallet_service_producer.dto.request.CreateWalletRequestDto;
import com.app.server.util.wallet_service_producer.dto.response.WalletResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.Callable;


@Component
@RequiredArgsConstructor
public class CreateWalletEvent implements Callable<String> {

    private WalletRMQProducer walletRMQProducer;

    @Override
    public String call() throws Exception {
        return createWallet();
    }

    public String createWallet(){
        CreateWalletRequestDto req = CreateWalletRequestDto.builder()
                .sub("")
                .balance(BigDecimal.ZERO)
                .currency("IRT")
                .build();
        WalletResponseDto res = walletRMQProducer.createWallet(req);
        Map<String,Object> data = (Map<String, Object>) res.getData();
        String sub = data.get("sub").toString();
        return sub;
    }

}