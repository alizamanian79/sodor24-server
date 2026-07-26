package com.app.server.controller;

import com.app.server.dto.response.Sodor24ResponseDto;
import com.app.server.model.NotificationType;
import com.app.server.service.NotificationService;
import com.app.server.service.impliment.NotificationFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity
            <Sodor24ResponseDto<String>> sendNotification(@RequestParam String receiver,
                                                               @RequestParam NotificationType type,
                                                               @RequestParam String message
                                               ){

        NotificationService service = notificationFactory.getService(type);
        service.sendNotification(receiver,message);
        return Sodor24ResponseDto.response("","پیام از طریق \s" +type.toString()+"\s رسال شد","","", HttpStatus.OK);

    }

}
