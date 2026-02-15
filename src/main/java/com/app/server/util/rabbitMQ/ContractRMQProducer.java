package com.app.server.util.rabbitMQ;

import com.app.server.exception.AppBadRequestException;
import com.app.server.util.rabbitMQ.dto.request.ContractRequestDto;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public ContractRMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public Object sendAndReceive(ContractRequestDto contractRequestDto) {
        log.info("Sending message to RabbitMQ -> {}", contractRequestDto);

        Map<String, Object> payload = new HashMap<>();
        payload.put("keyPassword", contractRequestDto.getKeyPassword());
        payload.put("reason", contractRequestDto.getReason());
        payload.put("country", contractRequestDto.getCountry());

        // Encode files if exist
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
            log.error("Error reading files for contract request: {}", e.getMessage(), e);
            throw new AppBadRequestException("خطا در خواندن فایل‌ها");
        }

        // Send and receive with error handling
        Object res;
        try {
            res = rabbitTemplate.convertSendAndReceive(exchange, contractRoutingKey, payload);
        } catch (Exception e) {
            log.error("Error sending message to RabbitMQ: {}", e.getMessage(), e);
            throw new AppBadRequestException("مشکل در ارتباط با سرویس امضا");
        }

        if (res == null) {
            log.error("No response received from contract service");
            throw new AppBadRequestException("پاسخی از سرویس امضا دریافت نشد (Timeout یا خطا)");
        }

        try {
            return res;
        } catch (Exception e) {
            log.error("Error converting RabbitMQ response: {}", e.getMessage(), e);
            throw new AppBadRequestException("پاسخ دریافتی از سرویس امضا نامعتبر است");
        }



    }
}
