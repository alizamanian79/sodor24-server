package com.app.server.controller;

import com.app.server.model.Signature;
import com.app.server.service.SignatureService;
import com.app.server.util.ZarinpalPaymentService.service.ZarinpalPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.app.server.Utils.ZarinpalPaymentService.dto.ZarinpalPaymentResponse;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    @Value("${app.server.host}")
    private String serverHost;

    private final ZarinpalPaymentService zarinpalPaymentService;
    private final SignatureService signatureService;

    @GetMapping("/pay/signature")
    public ResponseEntity<?> signaturePayment(@RequestParam String slug) {
     Signature findSignature = signatureService.findSignatureByIdSlug(slug);
     ZarinpalPaymentResponse res = zarinpalPaymentService.payment(findSignature.getPrice(),
             serverHost+"/api/v1/payment/callback/signature?slug="+slug,"signatur payment",findSignature.getUser().getPhoneNumber(),"alizamanian@gmail.com");
     return ResponseEntity.ok(res);
    }

    @GetMapping("/callback/signature")
    public ResponseEntity<?> signaturePaymentVerify(@RequestParam String slug,
                                                    @RequestParam String Authority) {
        Signature findSignature = signatureService.findSignatureByIdSlug(slug);
        boolean res = zarinpalPaymentService.verifyPayment(Authority,findSignature.getPrice());
        if (res){
            Signature resSignature = signatureService.chargeSignature(slug);
            return ResponseEntity.ok(resSignature);
        }
        return ResponseEntity.ok("fail");
    }

}
