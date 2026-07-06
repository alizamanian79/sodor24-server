package com.app.server.util.wallet_service_producer;

import com.app.server.util.wallet_service_producer.dto.request.TransactionDateRangeRequest;
import com.app.server.util.wallet_service_producer.dto.response.WalletResponseDto;
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




    public WalletResponseDto transactionsList(){
        WalletResponseDto res = (WalletResponseDto)  rabbitTemplate.convertSendAndReceive(exchange,transactionListRoutingKey,"");
       return res;
    }


    public WalletResponseDto getTransactionBySlug(Integer slug){
        WalletResponseDto res =(WalletResponseDto) rabbitTemplate.convertSendAndReceive(exchange,getTransactionBySlugRoutingKey,slug);
        return res;
    }



    public WalletResponseDto getTransactionInDateRange(TransactionDateRangeRequest req){
        WalletResponseDto res = (WalletResponseDto)  rabbitTemplate.convertSendAndReceive(exchange,getTransactionInDateRoutingKey,req);
        return res;
    }

    public WalletResponseDto deleteTransactionBySlug(Integer slug) {
        WalletResponseDto res = (WalletResponseDto)  rabbitTemplate.convertSendAndReceive(exchange,deleteTransactionBySlugRoutingKey,slug);
        return res;
    }




}
