package com.app.server.controller.notifications;

import com.app.server.dto.response.CustomResponseDto;
import com.app.server.util.notification_service.NotificationRequestDto;
import com.app.server.util.notification_service.NotificationService;
import com.app.server.util.notification_service.NotificationType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;


    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/email")
    public
    ResponseEntity<?> sendToUserEmail(@Valid @RequestBody NotificationRequestDto req){
        notificationService.notify(new NotificationRequestDto(
                NotificationType.EMAIL,
                req.getRecipient(),
                req.getSubject(),
                req.getMessage()
        ));

        return ResponseEntity.accepted()
                .body("Email notification request submitted for: " + req.getRecipient().toString());
    }


    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/sms")
    public
    ResponseEntity<?> sendToUserPhone(@Valid @RequestBody NotificationRequestDto req){
        notificationService.notify(new NotificationRequestDto(
                NotificationType.SMS,
                req.getRecipient(),
                req.getSubject(),
                req.getMessage()
        ));

        return ResponseEntity.accepted()
                .body("sms notification request submitted for: " + req.getRecipient().toString());
    }


}
