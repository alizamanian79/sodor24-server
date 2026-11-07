package com.app.server.util.notification_service.service;

import com.app.server.util.notification_service.NotificationRequestDto;
import com.app.server.util.notification_service.NotificationSender;
import com.app.server.util.notification_service.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
public class DevSmsNotificationSender implements NotificationSender {

    @Override
    public NotificationType getType() {
        return NotificationType.SMS;
    }

    @Override
    public void send(NotificationRequestDto request) {
        log.info("[DEV] SMS to {}: {}", request.getRecipient(), request.getMessage());
    }
}