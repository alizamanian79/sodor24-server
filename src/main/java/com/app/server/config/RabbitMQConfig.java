package com.app.server.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {


    // Exchanger
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("exchange");
    }
    // Message Converter
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    // RabbitMQ Converter Message
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate tmp = new RabbitTemplate(connectionFactory);
        tmp.setMessageConverter(messageConverter());
        return tmp;
    }

    // Queue
    @Bean
    public Queue signatureQueue() {
        return new Queue("queue");
    }

    @Bean
    public Binding signatureBinding() {
        return BindingBuilder
                .bind(signatureQueue())
                .to(exchange())
                .with("queue");
    }



}
