package com.app.server.util.notification_service.service;

import com.app.server.util.notification_service.NotificationRequestDto;
import com.app.server.util.notification_service.NotificationSender;
import com.app.server.util.notification_service.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("dev")
public class DevEmailNotificationSender implements NotificationSender {

    private final JavaMailSender mailSender;

    @Value("${notification.mail.from}")
    private String from;


    @Override
    public NotificationType getType() {
        return NotificationType.EMAIL;
    }

    @Override
    public void send(NotificationRequestDto request) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(from);
        mail.setTo(request.getRecipient());
        mail.setSubject(request.getSubject());
        mail.setText(request.getMessage());

        mailSender.send(mail);
        log.info("Email sent to {}", request.getRecipient());
    }
}