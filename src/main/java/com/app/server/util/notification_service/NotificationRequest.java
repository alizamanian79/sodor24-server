package com.app.server.util.notification_service;

// NotificationRequest.java
public record NotificationRequest(
        NotificationType type,
        String recipient,   // email address or phone number
        String subject,     // used by EMAIL only, ignored by SMS
        String message
) {}