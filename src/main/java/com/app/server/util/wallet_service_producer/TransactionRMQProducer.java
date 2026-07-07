package com.app.server.util.wallet_service_producer;

import com.app.server.util.wallet_service_producer.dto.request.TransactionDateRangeRequest;
import com.app.server.util.wallet_service_producer.dto.response.WalletResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionRMQProducer {



    @Value("${application.wallet-service.rabbitmq.routing.transaction-list}")
    private String transactionListRoutingKey;


    @Value("${application.wallet-service.rabbitmq.routing.get-transaction-by-slug}")
    private String getTransactionBySlugRoutingKey;



    @Value("${application.wallet-service.rabbitmq.routing.get-transaction-in-date}")
    private String getTransactionInDateRoutingKey;

    @Value("${application.wallet-service.rabbitmq.routing.delete-transaction-by-slug}")
    private String deleteTransactionBySlugRoutingKey;


    @Value("${application.wallet-service.rabbitmq.exchange}")
    private String exchange;


    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;



    private <T> T send(String routingKey, Object payload, Class<T> responseType) {
        Object raw = rabbitTemplate.convertSendAndReceive(exchange, routingKey, payload);
        if (raw == null) return null;
        return objectMapper.convertValue(raw, responseType);
    }


    public WalletResponseDto transactionsList(){
       return send(transactionListRoutingKey,"",WalletResponseDto.class);
    }


    public WalletResponseDto getTransactionBySlug(Integer slug){
        return send(getTransactionBySlugRoutingKey,slug,WalletResponseDto.class);
    }



    public WalletResponseDto getTransactionInDateRange(TransactionDateRangeRequest req){
        return send(getTransactionInDateRoutingKey,req,WalletResponseDto.class);
    }



    public WalletResponseDto deleteTransactionBySlug(Integer slug) {
       return send(deleteTransactionBySlugRoutingKey,slug,WalletResponseDto.class);
    }




}
