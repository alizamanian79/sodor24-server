package com.app.server.util.signature_service_producer.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfigSignature {

    @Value("${app.rabbitmq.queues.signature}")
    private String signatureQueue;

    @Bean
    public Queue signatureQueue() {
        return new Queue(signatureQueue, true);
    }

}
