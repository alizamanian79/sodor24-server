package com.app.server.util.notification_service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// NotificationSenderFactory.java
@Component
public class NotificationSenderFactory {

    private final Map<NotificationType, NotificationSender> senders;

    // Spring injects all NotificationSender beans automatically
    public NotificationSenderFactory(List<NotificationSender> senderList) {
        this.senders = senderList.stream()
                .collect(Collectors.toMap(
                        NotificationSender::getType,
                        Function.identity()
                ));
    }

    public NotificationSender getSender(NotificationType type) {
        NotificationSender sender = senders.get(type);
        if (sender == null) {
            throw new IllegalArgumentException("Unsupported notification type: " + type);
        }
        return sender;
    }
}