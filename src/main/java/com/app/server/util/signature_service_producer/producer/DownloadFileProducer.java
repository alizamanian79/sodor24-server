package com.app.server.util.signature_service_producer.producer;

import com.app.server.util.signature_service_producer.dto.request.DownloadFileRequestDto;
import com.app.server.util.signature_service_producer.dto.response.DownloadFileResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class DownloadFileProducer {

    @Value("${application.signature-service.rabbitmq.exchange}")
    private String exchange;

    @Value("${application.signature-service.rabbitmq.routing-keys.download}")
    private String downloadRoutingKey;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;



    private <T> T send(String routingKey, Object payload, Class<T> responseType) {
        Object raw = rabbitTemplate.convertSendAndReceive(exchange, routingKey, payload);
        if (raw == null) return null;
        return objectMapper.convertValue(raw, responseType);
    }


    public DownloadFileResponseDto download(DownloadFileRequestDto req){
        DownloadFileResponseDto res = send(downloadRoutingKey,req, DownloadFileResponseDto.class);
        return res;
    }

    //    it should be like this post for downloading
//    @PostMapping
//    public ResponseEntity<ByteArrayResource> download(
//            @RequestBody DownloadFileRequestDto req) {
//
//        DownloadFileResponseDto res = producer.download(req);
//
//        ByteArrayResource resource = new ByteArrayResource(res.getContent());
//
//        return ResponseEntity.ok()
//                .contentLength(res.getContent().length)
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .header(HttpHeaders.CONTENT_DISPOSITION,
//                        ContentDisposition.attachment()
//                                .filename(res.getFileName())
//                                .build()
//                                .toString())
//                .body(resource);
//    }

}
