package com.app.server.util.zarinpalPaymentService.controller;



import com.app.server.util.zarinpalPaymentService.dto.ZarinpalPaymentRequest;
import com.app.server.util.zarinpalPaymentService.dto.ZarinpalPaymentResponse;
import com.app.server.util.zarinpalPaymentService.dto.ZarinpalPaymentVerifyRequest;
import com.app.server.util.zarinpalPaymentService.service.ZarinpalPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/zarinpal")
public class ZarinpalPaymentController {

    @Autowired
    private ZarinpalPaymentService zarinpalPaymentService;

    @PostMapping("/payment")
    public ResponseEntity<?> zarinpalPayment(@RequestBody ZarinpalPaymentRequest req) {
        System.out.println(req);
        ZarinpalPaymentResponse res = zarinpalPaymentService.payment(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> zarinpalPaymentVerify(@RequestBody ZarinpalPaymentVerifyRequest req) {
        System.out.println(req);
        Boolean res = zarinpalPaymentService.verifyPayment(req.getAuthority(),req.getAmount());
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


}
