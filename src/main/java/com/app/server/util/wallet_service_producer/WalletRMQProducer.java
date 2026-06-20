package com.app.server.util.wallet_service_producer;

import com.app.server.util.wallet_service_producer.dto.request.*;
import com.app.server.util.wallet_service_producer.dto.response.CustomResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalletRMQProducer {

    @Value("${application.wallet-service.rabbitmq.exchange}")
    public String exchange;

    @Value("${application.wallet-service.rabbitmq.routing.create-wallet}")
    public String createWalletRoutingKey;

    @Value("${application.wallet-service.rabbitmq.routing.get-wallet}")
    private String getWalletRoutingKey;

    @Value("${application.wallet-service.rabbitmq.routing.active-wallet}")
    private String activeWalletRoutingKey;


    @Value("${application.wallet-service.rabbitmq.routing.list-wallet}")
    private String listWalletRoutingKey;


    @Value("${application.wallet-service.rabbitmq.routing.delete-wallet}")
    private String deleteWalletRoutingKey;

    @Value("${application.wallet-service.rabbitmq.routing.update-sub-wallet}")
    private String updateWalletRoutingKey;


    @Value("${application.wallet-service.rabbitmq.routing.payment-request-wallet}")
    private String paymentRequestWalletRoutingKey;



    @Value("${application.wallet-service.rabbitmq.routing.payment-verifier-wallet}")
    private String paymentVerifierWalletRoutingKey;


    private final RabbitTemplate rabbitTemplate;


    public CustomResponseDto walletLists(){
        CustomResponseDto res = (CustomResponseDto) rabbitTemplate.convertSendAndReceive(
                exchange,
                listWalletRoutingKey,
                ""
        );
        return res;
    }

    public CustomResponseDto createWallet(CreateWalletRequestDto req){
        CustomResponseDto res = (CustomResponseDto) rabbitTemplate.convertSendAndReceive(
                exchange,
                createWalletRoutingKey,
                req
        );
        return res;
    }

    public CustomResponseDto getWalletBySub(String sub){
        CustomResponseDto res = (CustomResponseDto) rabbitTemplate.convertSendAndReceive(
                exchange,
                getWalletRoutingKey,
                sub
        );
        return res;
    }

    public CustomResponseDto setActive(ActivityRequestDto req){
        CustomResponseDto res = (CustomResponseDto) rabbitTemplate.convertSendAndReceive(
                exchange,
                activeWalletRoutingKey,
                req
        );
        return res;
    }




    public CustomResponseDto deleteWalletBySub(String sub){
        CustomResponseDto res = (CustomResponseDto) rabbitTemplate.convertSendAndReceive(
                exchange,
                deleteWalletRoutingKey,
                sub
        );
        return res;
    }

    public CustomResponseDto updateWalletSub(UpdateSubRequestDto req){
        CustomResponseDto res = (CustomResponseDto) rabbitTemplate.convertSendAndReceive(
                exchange,
                updateWalletRoutingKey,
                req
        );
        return res;
    }

    public CustomResponseDto paymentRequest(PaymentRequestDto req){
        CustomResponseDto res = (CustomResponseDto) rabbitTemplate.convertSendAndReceive(
                exchange,
                paymentRequestWalletRoutingKey,
                req
        );
        return res;
    }

    public CustomResponseDto paymentVerifier(PaymentVerifierRequestDto req){
        CustomResponseDto res = (CustomResponseDto) rabbitTemplate.convertSendAndReceive(
                exchange,
                paymentVerifierWalletRoutingKey,
                req
        );
        return res;
    }
}