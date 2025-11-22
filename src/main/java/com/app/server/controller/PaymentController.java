package com.app.server.controller;

import com.app.server.util.ZarinpalPaymentService.service.ZarinpalPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private Long signaturePrice=15000L;
    private final ZarinpalPaymentService zarinpalPaymentService;

    @GetMapping("/signature")
    public ResponseEntity<?> signature(@RequestParam Long days) {
       com.app.server.Utils.ZarinpalPaymentService.dto.ZarinpalPaymentResponse res =  zarinpalPaymentService.payment(signaturePrice*days,"http://google.com","this is signature payment","09917403979","alizamaniandev@gmail.com");
    return new ResponseEntity<>(res, HttpStatus.OK);
    }





}
