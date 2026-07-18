package com.app.server.util.signature_service_producer.producer;

import com.app.server.util.signature_service_producer.dto.request.SignatureRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignatureProducer {
    @Value("${application.signature-service.rabbitmq.exchange}")
    private String exchange;

    @Value("${application.signature-service.rabbitmq.routing-keys.signature}")
    private String signatureRoutingKey;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;


    private <T> T send(String routingKey, Object payload, Class<T> responseType) {
        Object raw = rabbitTemplate.convertSendAndReceive(exchange, routingKey, payload);
        if (raw == null) return null;
        return objectMapper.convertValue(raw, responseType);
    }

    public Object generateSignature(SignatureRequestDto req){
        return send(signatureRoutingKey,req,Object.class);
    }


}
