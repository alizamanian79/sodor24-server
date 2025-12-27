package com.app.server.controller;

import com.app.server.dto.request.SignatureRequest;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.dto.signatureDto.SignatureRequestDto;
import com.app.server.dto.signatureDto.SignatureResponseDto;
import com.app.server.model.UserSignature;
import com.app.server.service.JwtService;
import com.app.server.service.UserSignatureService;
import com.app.server.util.ZarinpalPaymentService.dto.ZarinpalPaymentRequest;
import com.app.server.util.ZarinpalPaymentService.dto.ZarinpalPaymentResponse;
import com.app.server.util.ZarinpalPaymentService.service.ZarinpalPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/service/signature")
public class UserSignatureController {

    @Value("${app.server.host}")
    private String serverHost;

    private final JwtService jwtService;
    private final UserSignatureService userSignatureService;
    private final ZarinpalPaymentService zarinpalPaymentService;


    @PostMapping
    public ResponseEntity<?> buySignature(
            @RequestBody SignatureRequest req,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Access Token ارسال نشده است");
        }

        String accessToken = authorization.replace("Bearer ", "");
        System.out.println(accessToken);

        UserSignature signature = userSignatureService.generateUserSignature(req);

        if (signature.isValid()) {
            return ResponseEntity.ok(
                    CustomResponseDto.builder()
                            .message("این امضا خریداری شده است و هنوز اعتبار دارد")
                            .build()
            );
        }

        if (signature != null) {
            String otp = signature.getOtp();

            ZarinpalPaymentRequest paymentReq = ZarinpalPaymentRequest.builder()
                    .email(req.getEmail())
                    .mobile(signature.getUser().getPhoneNumber())
                    .amount(signature.getSignature().getPrice())
                    .description("خرید سرویس امضای " + signature.getSignature().getTitle())
                    .callback_url(
                            serverHost +
                                    "/api/v1/service/signature/verify" +
                                    "?otp=" + otp +
                                    "&access_token=" + accessToken
                    )
                    .build();

            ZarinpalPaymentResponse res =
                    zarinpalPaymentService.payment(paymentReq);

            return ResponseEntity.ok(res);
        }

        return ResponseEntity.badRequest().build();
    }



    @GetMapping("/verify")
    public ResponseEntity<?> verify(Authentication authentication) {
        try {
            Object auth = authentication.getPrincipal();
            return new ResponseEntity<>(auth, HttpStatus.OK);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }



    @GetMapping
    public List<UserSignature> list(){
        return userSignatureService.findAll();
    }

    @GetMapping("/{id}")
    public UserSignature get(@PathVariable Long id){
        return userSignatureService.findById(id);
    }




//    @PostMapping("/test")
//    public SignatureResponseDto test(@RequestBody SignatureRequestDto req) {
//        SignatureResponseDto res = userSignatureService.sendSignatureRequest(req);
//        return res;
//    }



}
