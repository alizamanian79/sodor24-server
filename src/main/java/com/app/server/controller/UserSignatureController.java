package com.app.server.controller;

import com.app.server.dto.request.SignatureRequest;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.model.UserSignature;
import com.app.server.service.SignaturePlanService;
import com.app.server.service.UserSignatureService;
import com.app.server.util.rabbitMQ.SignatureRMQProducer;
import com.app.server.util.zarinpalPaymentService.dto.ZarinpalPaymentRequest;
import com.app.server.util.zarinpalPaymentService.dto.ZarinpalPaymentResponse;
import com.app.server.util.zarinpalPaymentService.service.ZarinpalPaymentService;
import com.github.mfathi91.time.PersianDate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/service/signature")
public class UserSignatureController {

    private final SignaturePlanService signaturePlanService;
    @Value("${app.server.host}")
    private String serverHost;

    private final UserSignatureService userSignatureService;
    private final ZarinpalPaymentService zarinpalPaymentService;

    private final SignatureRMQProducer signatureRMQProducer;


    @PostMapping()
     ResponseEntity<?> buySignature(
            @RequestBody SignatureRequest req,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Access Token ارسال نشده است");
        }


        UserSignature signature = userSignatureService.generateUserSignature(req);

        if (signature.isValid()) {
            return ResponseEntity.ok(
                    CustomResponseDto.builder()
                            .message("این امضا خریداری شده است و هنوز اعتبار دارد")
                            .build()
            );
        }

        if (signature != null) {
            CustomResponseDto res =CustomResponseDto.builder()
                    .message("کد تایید به شماره تماس شما فرستاده شد")
                    .details("")
                    .timestamp(PersianDate.now())
                    .build();
            return ResponseEntity.ok(res);
        }

        return ResponseEntity.badRequest().build();
    }








    @GetMapping
    public List<UserSignature> list(){
        return userSignatureService.findAll();
    }

    @GetMapping("/{id}")
    public UserSignature get(@PathVariable Long id){
        return userSignatureService.findById(id);
    }



    // Verify Otp
    @GetMapping("/verify/otp/{otp}")
    public ResponseEntity<?> verifyOtp(@PathVariable String otp,
                                             @RequestHeader(value = "Authorization", required = false)
                                             String authorization
    ) throws Exception{
      UserSignature signature =  userSignatureService.findUserSignatureByOtp(otp);
      String accessToken = authorization.replace("Bearer ", "");

      // Check Otp Before Gatway send to user
      if (signature.getOtp().equals(null)) {
          CustomResponseDto res = CustomResponseDto.builder()
                  .message("امضا شما قبلا تاییده شده.")
                  .details("")
                  .timestamp(PersianDate.now())
                  .build();
          return new ResponseEntity<>(res, HttpStatus.OK);
      }

      ZarinpalPaymentRequest paymentReq = ZarinpalPaymentRequest.builder()
                .email(signature.getEmail())
                .mobile(signature.getUser().getPhoneNumber())
                .amount(signature.getSignature().getPrice())
                .description("خرید سرویس امضای " + signature.getSignature().getTitle())
                .callback_url(
                        serverHost +
                                "/api/v1/service/signature/callback" +
                                "?otp=" + otp +
                                "&token=" + accessToken
                )
                .build();

        ZarinpalPaymentResponse res =
                zarinpalPaymentService.payment(paymentReq);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }



    // Verify transaction
    @GetMapping("/callback")
    public ResponseEntity<?> verify(@RequestParam String otp,
                                    @RequestParam String Authority,
                                    @RequestParam String Status) {
        UserSignature find = userSignatureService.findUserSignatureByOtp(otp);

        // Validate transaction payment
        if (Authority == null || Authority.isBlank()
                    || Status == null || Status.isBlank()
                    || !"OK".equals(Status)) {
                return new ResponseEntity<>("پرداخت ناموفق بود", HttpStatus.BAD_REQUEST);
            }

        boolean payStatus = zarinpalPaymentService.verifyPayment(
                    Authority,
                    find.getSignature().getPrice()
            );

            if (!payStatus) {
                return new ResponseEntity<>("پرداخت ناموفق بود", HttpStatus.BAD_REQUEST);
            }

            CustomResponseDto res = userSignatureService.verifySignature(find.getOtp());
            userSignatureService.sendRequestToSignatureService(find);
            res.setMessage("پرداخت با موفقیت انجام شد");
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);


    }


//    @PostMapping("/test")
//    public SignatureResponseDto test(@RequestBody SignatureRequestDto req) {
//        SignatureResponseDto res = userSignatureService.sendSignatureRequest(req);
//        return res;
//    }



}
