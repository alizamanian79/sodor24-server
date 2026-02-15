package com.app.server.util.rabbitMQ;


import com.app.server.exception.AppBadRequestException;
import com.app.server.util.rabbitMQ.dto.request.SignatureRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SignatureRMQProducer {

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routing-keys.signature}")
    private String signatureRoutingKey;

    private static final Logger log =
            LoggerFactory.getLogger(SignatureRMQProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public SignatureRMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public Object sendAndReceive(SignatureRequestDto message) {
        log.info("Sending message -> {}", message);

        Object res;
        try {
            res = rabbitTemplate.convertSendAndReceive(exchange, signatureRoutingKey, message);
        } catch (Exception e) {
            log.error("خطا در ارسال یا دریافت پیام از RabbitMQ: {}", e.getMessage(), e);
            throw new AppBadRequestException("مشکل در ارتباط با سرویس RabbitMQ");
        }

        if (res == null) {
            log.error("پاسخی از سرویس تولید امضا دریافت نشد");
            throw new AppBadRequestException("پاسخی از سرویس تولید امضا دریافت نشد (Timeout یا خطای سرویس دیگر)");
        }


        try {
            return res;
        } catch (Exception e) {
            log.error("خطا در تبدیل پاسخ از سرویس RabbitMQ: {}", e.getMessage(), e);
            throw new AppBadRequestException("پاسخ دریافتی از سرویس امضا نامعتبر است");
        }

    }

}
