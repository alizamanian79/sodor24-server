package com.app.server.util.wallet_service_producer.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfigWallet {

    @Value("${application.wallet-service.rabbitmq.exchange}")
    private String exchange;


    @Value("${application.wallet-service.rabbitmq.queue.create-wallet}")
    private String createWalletQueue;
    @Value("${application.wallet-service.rabbitmq.routing.create-wallet}")
    private String createWalletRoutingKey;


    @Value("${application.wallet-service.rabbitmq.queue.get-wallet}")
    private String getWalletQueue;
    @Value("${application.wallet-service.rabbitmq.routing.get-wallet}")
    private String getWalletRoutingKey;


    @Value("${application.wallet-service.rabbitmq.queue.active-wallet}")
    private String activeWalletQueue;
    @Value("${application.wallet-service.rabbitmq.routing.active-wallet}")
    private String activeWalletRoutingKey;





    @Value("${application.wallet-service.rabbitmq.queue.list-wallet}")
    private String listWalletQueue;
    @Value("${application.wallet-service.rabbitmq.routing.list-wallet}")
    private String listWalletRoutingKey;


    @Value("${application.wallet-service.rabbitmq.queue.delete-wallet}")
    private String deleteWalletQueue;
    @Value("${application.wallet-service.rabbitmq.routing.delete-wallet}")
    private String deleteWalletRoutingKey;

    @Value("${application.wallet-service.rabbitmq.queue.update-sub-wallet}")
    private String updateWalletQueue;
    @Value("${application.wallet-service.rabbitmq.routing.update-sub-wallet}")
    private String updateWalletRoutingKey;


    @Value("${application.wallet-service.rabbitmq.queue.payment-request-wallet}")
    private String paymentRequestWalletQueue;
    @Value("${application.wallet-service.rabbitmq.routing.payment-request-wallet}")
    private String paymentRequestWalletRoutingKey;

    @Value("${application.wallet-service.rabbitmq.queue.payment-verifier-wallet}")
    private String paymentVerifierWalletQueue;
    @Value("${application.wallet-service.rabbitmq.routing.payment-verifier-wallet}")
    private String paymentVerifierWalletRoutingKey;

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(exchange);
    }




    @Bean
    public Queue createWalletQueue() {
        return new Queue(createWalletQueue, true);
    }
    @Bean
    public Binding createWalletBinding() {
        return BindingBuilder
                .bind(createWalletQueue())
                .to(topicExchange())
                .with(createWalletRoutingKey);
    }




    @Bean
    public Queue getWalletQueue() {
        return new Queue(getWalletQueue, true);
    }
    @Bean
    public Binding getWalletBinding() {
        return BindingBuilder
                .bind(getWalletQueue())
                .to(topicExchange())
                .with(getWalletRoutingKey);
    }




    @Bean
    public Queue activeWalletQueue() {
        return new Queue(activeWalletQueue, true);
    }
    @Bean
    public Binding activeWalletBinding() {
        return BindingBuilder
                .bind(activeWalletQueue())
                .to(topicExchange())
                .with(activeWalletRoutingKey);
    }





    @Bean
    public Queue listWalletQueue() {
        return new Queue(listWalletQueue, true);
    }
    @Bean
    public Binding listWalletBinding() {
        return BindingBuilder
                .bind(listWalletQueue())
                .to(topicExchange())
                .with(listWalletRoutingKey);
    }




    @Bean
    public Queue deleteWalletQueue() {
        return new Queue(deleteWalletQueue, true);
    }
    @Bean
    public Binding deleteWalletBinding() {
        return BindingBuilder
                .bind(deleteWalletQueue())
                .to(topicExchange())
                .with(deleteWalletRoutingKey);
    }


    @Bean
    public Queue updateWalletQueue() {
        return new Queue(updateWalletQueue, true);
    }
    @Bean
    public Binding updateWalletBinding() {
        return BindingBuilder
                .bind(updateWalletQueue())
                .to(topicExchange())
                .with(updateWalletRoutingKey);
    }


    @Bean
    public Queue paymentRequestWalletQueue() {
        return new Queue(paymentRequestWalletQueue, true);
    }
    @Bean
    public Binding paymentWalletBinding() {
        return BindingBuilder
                .bind(paymentRequestWalletQueue())
                .to(topicExchange())
                .with(paymentRequestWalletRoutingKey);
    }


    @Bean
    public Queue paymentVerifierWalletQueue() {
        return new Queue(paymentVerifierWalletQueue, true);
    }
    @Bean
    public Binding paymentVerifierWalletBinding() {
        return BindingBuilder
                .bind(paymentVerifierWalletQueue())
                .to(topicExchange())
                .with(paymentVerifierWalletRoutingKey);
    }






}