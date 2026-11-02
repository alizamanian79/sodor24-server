package com.app.server.util.notification_service;

// NotificationSender.java
public interface NotificationSender {

    NotificationType getType();

    void send(NotificationRequest request);
}