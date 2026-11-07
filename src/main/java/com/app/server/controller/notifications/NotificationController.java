package com.app.server.controller.notifications;

import com.app.server.dto.response.CustomResponseDto;
import com.app.server.util.notification_service.NotificationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CustomResponseDto sendNotificationSMS(@Valid @RequestBody NotificationRequest req){

    }
}
