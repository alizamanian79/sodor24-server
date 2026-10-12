package com.app.server.controller.notifications;

import com.app.server.dto.response.CustomResponseDto;
import com.app.server.model.NotificationType;
import com.app.server.service.NotificationService;
import com.app.server.service.impliment.NotificationFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification/send")
public class NotificationController {
    private final NotificationFactory notificationFactory;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> sendNotification(@RequestParam String receiver,
                                                         @RequestParam NotificationType type,
                                                         @RequestParam String message
                                               ){

        NotificationService service = notificationFactory.getService(type);
        service.sendNotification(receiver,message);

        CustomResponseDto res = CustomResponseDto.builder()
                .data(null)
                .message("پیام از طریق \s" +type.toString()+"\s رسال شد")
                .details(null)
                .status(201)
                .build();
        return ResponseEntity.status(res.getStatus()).body(res);

    }

}
