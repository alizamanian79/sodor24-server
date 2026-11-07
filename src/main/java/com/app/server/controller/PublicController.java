package com.app.server.controller;

import com.app.server.util.notification_service.NotificationRequest;
import com.app.server.util.notification_service.NotificationService;
import com.app.server.util.notification_service.NotificationType;
import com.app.server.util.signature_service_producer.CertificateProducer;
import com.app.server.util.signature_service_producer.dto.request.ca.CreateCompanyCaRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public")
public class PublicController {

    private final NotificationService notificationService;
    private final CertificateProducer certificateProducer;

    @PostMapping("/notification")
    public ResponseEntity<String> sendNotification(
            @RequestParam String email,
            @RequestParam String subject,
            @RequestParam String msg
    ) {
        notificationService.notify(new NotificationRequest(
                NotificationType.EMAIL,
                email,
                subject,
                msg
        ));

        return ResponseEntity.accepted()
                .body("Email notification request submitted for: " + email);
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
