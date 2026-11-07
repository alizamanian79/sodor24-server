package com.app.server.util.notification_service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// NotificationService.java
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationSenderFactory factory;

    public void notify(NotificationRequestDto request) {
        factory.getSender(request.getType()).send(request);
    }
}