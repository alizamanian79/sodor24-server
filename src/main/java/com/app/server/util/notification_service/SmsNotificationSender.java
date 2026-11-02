package com.app.server.util.notification_service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// SmsNotificationSender.java
@Slf4j
@Component
public class SmsNotificationSender implements NotificationSender {

    @Override
    public NotificationType getType() {
        return NotificationType.SMS;
    }

    @Override
    public void send(NotificationRequest request) {
        // TODO: call your SMS provider API here
        log.info("SMS sent to {}", request.recipient());
    }
}