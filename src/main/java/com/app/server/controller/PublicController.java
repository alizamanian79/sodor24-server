package com.app.server.controller;

import com.app.server.util.notification_service.NotificationRequest;
import com.app.server.util.notification_service.NotificationService;
import com.app.server.util.notification_service.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public")
public class PublicController {

    private final NotificationService notificationService;

    @GetMapping("/notification")
    public String sendNotification(@RequestParam String email,
                                   @RequestParam String subject ,
                                   @RequestParam String msg){
        notificationService.notify(new NotificationRequest(
                NotificationType.EMAIL,
                email,
                subject,
                msg
        ));

        return "sent email to \s" + email;

    }

}
