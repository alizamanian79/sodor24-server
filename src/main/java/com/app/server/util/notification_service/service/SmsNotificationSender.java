package com.app.server.util.notification_service.service;

import com.app.server.util.notification_service.NotificationRequestDto;
import com.niazpardaz.sms.client.*;
import com.niazpardaz.sms.models.*;
import com.app.server.util.notification_service.NotificationSender;
import com.app.server.util.notification_service.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

// SmsNotificationSender.java
@Slf4j
@Component
@Profile("prod")
public class SmsNotificationSender implements NotificationSender {

    @Value("${niazpardaz.sms.api-key}")
    private String apiKey;

    @Value("${niazpardaz.sender.number}")
    private String niazpardazSenderNumber;


    @Override
    public NotificationType getType() {
        return NotificationType.SMS;
    }

    @Override
    public void send(NotificationRequestDto request) {
        // TODO: call your SMS provider API here


        NiazpardazSmsClient client = NiazpardazSmsClientBuilder
                .create(apiKey)
                .build();
        SendBatchSmsResult result = client.send(niazpardazSenderNumber, request.getRecipient(),
                request.getSubject() +
                        "\n"+
                        request.getMessage()
        );

        log.info("SMS sent to {}", request.getRecipient());
    }
}