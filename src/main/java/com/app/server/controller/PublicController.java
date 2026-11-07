package com.app.server.controller;


import com.app.server.util.notification_service.NotificationRequestDto;
import com.app.server.util.notification_service.NotificationService;
import com.app.server.util.notification_service.NotificationType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public")
public class PublicController {

    private final NotificationService notificationService;

    @PostMapping("/notification/email")
    public ResponseEntity<String> sendNotification(
            @Valid
            @RequestParam String email,
            @RequestParam String subject,
            @RequestParam String msg
    ) {
        notificationService.notify(new NotificationRequestDto(
                NotificationType.EMAIL,
                email,
                subject,
                msg
        ));

        return ResponseEntity.accepted()
                .body("Email notification request submitted for: " + email);
    }


    @PostMapping("/notification/sms")
    public ResponseEntity<String> sendNotificationSms(
            @Valid
            @RequestParam String phone,
            @RequestParam String subject,
            @RequestParam String msg
    ) {
        notificationService.notify(new NotificationRequestDto(
                NotificationType.SMS,
                phone,
                subject,
                msg
        ));

        return ResponseEntity.accepted()
                .body("Email notification request submitted for: " + phone);
    }


//    @GetMapping("/test")
//    public Map<String, Object> test(){
//
//        CreateCompanyCaRequestDto req = CreateCompanyCaRequestDto.builder()
//                        .companyId("zilan")
//                        .country("iran")
//                        .companyName("zilan")
//                        .privateKeyPassword("88770022$sasl")
//                        .location("iran")
//                        .state("tehran")
//                        .validityDays(60)
//                        .build();
//
//     return    certificateProducer.createCompanyCa(req);
//
//    }
//
//
//    @GetMapping("/test/{id}")
//    public Map<String, Object> test(@PathVariable String id){
//
//        Map<String,String> req = new HashMap<>();
//        req.put("companyId",id);
//        return    certificateProducer.getCompany(req);
//    }

}
