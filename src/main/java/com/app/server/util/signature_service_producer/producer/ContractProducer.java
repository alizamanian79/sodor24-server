package com.app.server.util.signature_service_producer.producer;

import com.app.server.util.signature_service_producer.dto.request.ContractRabbitDto;
import com.app.server.util.signature_service_producer.dto.request.ContractRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractProducer {

    @Value("${application.signature-service.rabbitmq.exchange}")
    private String exchange;

    @Value("${application.signature-service.rabbitmq.routing-keys.contract}")
    private String contractRoutingKey;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;


    private <T> T send(String routingKey, Object payload, Class<T> responseType) {
        Object raw = rabbitTemplate.convertSendAndReceive(exchange, routingKey, payload);
        if (raw == null) return null;
        return objectMapper.convertValue(raw, responseType);
    }


    public Object createOrSignedContract(ContractRequestDto req) throws IOException {

        ContractRabbitDto dto = ContractRabbitDto.builder()
                .fileName(req.getFile().getOriginalFilename())
                .file(Base64.getEncoder().encodeToString(req.getFile().getBytes()))
                .keyFileName(req.getPrivateKeyFile().getOriginalFilename())
                .privateKeyFile(Base64.getEncoder().encodeToString(req.getPrivateKeyFile().getBytes()))
                .keyPassword(req.getKeyPassword())
                .reason(req.getReason())
                .country(req.getCountry())
                .build();

        return send(contractRoutingKey, dto, Object.class);
    }

}
