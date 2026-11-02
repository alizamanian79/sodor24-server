package com.app.server.util.notification_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

// EmailNotificationSender.java
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationSender implements NotificationSender {

    private final JavaMailSender mailSender;

    @Override
    public NotificationType getType() {
        return NotificationType.EMAIL;
    }

    @Override
    public void send(NotificationRequest request) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(request.recipient());
        mail.setSubject(request.subject());
        mail.setText(request.message());

        mailSender.send(mail);
        log.info("Email sent to {}", request.recipient());
    }
}