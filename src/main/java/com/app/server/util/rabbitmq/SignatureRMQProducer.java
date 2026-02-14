

import app.signature.service.dto.request.SignatureRequestDto;
import app.signature.service.dto.response.RMQError;
import app.signature.service.dto.response.RMQResponse;
import app.signature.service.dto.response.SignatureResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper mapper = new ObjectMapper();

    public SignatureRMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public SignatureResponseDto sendAndReceive(SignatureRequestDto message) {

        log.info("Message sent successfully -> {}", message);

        Object res = rabbitTemplate.convertSendAndReceive(
                exchange, signatureRoutingKey, message
        );

        if (res == null) {
            throw new AppInternalException(
                    "پاسخی از سرویس تولید امضا دریافت نشد");
        }

        RMQResponse<?> response =
                mapper.convertValue(res, RMQResponse.class);

        if (!response.isSuccess()) {
            RMQError err = response.getError();
            throw new AppBadRequestException(err.getMessage());
        }

        return mapper.convertValue(
                response.getData(), SignatureResponseDto.class);
    }
}
