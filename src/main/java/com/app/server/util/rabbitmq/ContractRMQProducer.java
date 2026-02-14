package com.app.server.util.rabbitmq;

import app.signature.service.dto.request.ContractRequestDto;
import app.signature.service.dto.response.ContractResponseDto;
import app.signature.service.dto.response.RMQError;
import app.signature.service.dto.response.RMQResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.app.server.exception.AppNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ContractRMQProducer {

    @Value("${app.rabbitmq.exchange}")
    private String exchange;
    @Value("${app.rabbitmq.routing-keys.contract}")
    private String contractRoutingKey;

    private static final Logger log = LoggerFactory.getLogger(ContractRMQProducer.class);
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public ContractRMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public ContractResponseDto sendAndReceive(ContractRequestDto contractRequestDto) {

        log.info("Sending message to RabbitMQ -> {}", contractRequestDto);

        Map<String, Object> payload = new HashMap<>();
        payload.put("keyPassword", contractRequestDto.getKeyPassword());
        payload.put("reason", contractRequestDto.getReason());
        payload.put("country", contractRequestDto.getCountry());

        try {
            if (contractRequestDto.getFile() != null) {
                payload.put("file",
                        Base64.encodeBase64String(contractRequestDto.getFile().getBytes()));
                payload.put("fileName",
                        contractRequestDto.getFile().getOriginalFilename());
            }
            if (contractRequestDto.getPrivateKeyFile() != null) {
                payload.put("privateKeyFile",
                        Base64.encodeBase64String(contractRequestDto.getPrivateKeyFile().getBytes()));
                payload.put("keyFileName",
                        contractRequestDto.getPrivateKeyFile().getOriginalFilename());
            }
        } catch (IOException e) {
            throw new AppNotFoundException("خطا در خواندن فایل");
        }

        Object res = rabbitTemplate.convertSendAndReceive(
                exchange, contractRoutingKey, payload
        );

        if (res == null) {
            throw new AppNotFoundException();
        }

        RMQResponse<?> response = mapper.convertValue(res, RMQResponse.class);

        if (!response.isSuccess()) {
            RMQError err = response.getError();
            throw new AppBadRequestException(err.getMessage());
        }

        return mapper.convertValue(response.getData(), ContractResponseDto.class);
    }

}
