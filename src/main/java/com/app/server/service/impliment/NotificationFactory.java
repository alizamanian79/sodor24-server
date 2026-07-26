package com.app.server.service.impliment;

import com.app.server.model.NotificationType;
import com.app.server.service.NotificationService;
import org.springframework.stereotype.Component;

@Component
public class NotificationFactory {

    private final SMSServiceImpl smsService;
    private final EmailServiceImpl emailService;

    public NotificationFactory(SMSServiceImpl smsService,
                               EmailServiceImpl emailService) {
        this.smsService = smsService;
        this.emailService = emailService;
    }

    public NotificationService getService(NotificationType type) {

        return switch (type) {
            case SMS -> smsService;
            case EMAIL -> emailService;
        };
    }
}

// usage
//NotificationService service =
//        notificationFactory.getService(NotificationType.SMS);