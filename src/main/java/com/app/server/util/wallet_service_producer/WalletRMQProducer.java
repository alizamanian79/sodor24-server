package com.app.server.util.wallet_service_producer;

import com.app.server.util.wallet_service_producer.dto.request.*;
import com.app.server.util.wallet_service_producer.dto.response.WalletResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalletRMQProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

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


    private <T> T send(String routingKey, Object payload, Class<T> responseType) {
        Object raw = rabbitTemplate.convertSendAndReceive(exchange, routingKey, payload);
        if (raw == null) return null;
        return objectMapper.convertValue(raw, responseType);
    }

    public WalletResponseDto walletLists() {
        return send(listWalletRoutingKey, "", WalletResponseDto.class);
    }

    public WalletResponseDto createWallet(CreateWalletRequestDto req) {
        return send(createWalletRoutingKey, req, WalletResponseDto.class);
    }

    public WalletResponseDto getWalletBySub(String sub) {
        return send(getWalletRoutingKey, sub, WalletResponseDto.class);
    }

    public WalletResponseDto setActive(ActivityRequestDto req) {
        return send(activeWalletRoutingKey, req, WalletResponseDto.class);
    }

    public WalletResponseDto deleteWalletBySub(String sub) {
        return send(deleteWalletRoutingKey, sub, WalletResponseDto.class);
    }

    public WalletResponseDto updateWalletSub(UpdateSubRequestDto req) {
        return send(updateWalletRoutingKey, req, WalletResponseDto.class);
    }

    public WalletResponseDto paymentRequest(PaymentRequestDto req) {
        return send(paymentRequestWalletRoutingKey, req, WalletResponseDto.class);
    }

    public WalletResponseDto paymentVerifier(PaymentVerifierRequestDto req) {
        return send(paymentVerifierWalletRoutingKey, req, WalletResponseDto.class);
    }
}