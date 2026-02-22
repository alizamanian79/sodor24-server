package com.app.server.controller;

import com.app.server.dto.request.SignatureRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.model.Signature;
import com.app.server.model.User;
import com.app.server.service.SignaturePlanService;
import com.app.server.service.SignatureService;
import com.app.server.service.UserService;
import com.app.server.util.rabbitMQ.SignatureRMQProducer;
import com.app.server.util.zarinpalPaymentService.dto.ZarinpalPaymentRequest;
import com.app.server.util.zarinpalPaymentService.dto.ZarinpalPaymentResponse;
import com.app.server.util.zarinpalPaymentService.service.ZarinpalPaymentService;
import com.github.mfathi91.time.PersianDate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/signature/service")
public class SignatureController {

    private final SignaturePlanService signaturePlanService;
    @Value("${app.server.host}")
    private String serverHost;

    private final SignatureService signatureService;
    private final ZarinpalPaymentService zarinpalPaymentService;
    private final UserService userService;


    @PostMapping()
     ResponseEntity<?> signatureRequest(
            @Valid @RequestBody SignatureRequestDto req,
            Authentication auth,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Access Token ارسال نشده است");
        }


        User user = userService.convertUserFromAuthentication(auth);
        req.setUserId(user.getId());
        Signature signature = signatureService.generateSignature(req);

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


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Signature> signatureList(){
        return signatureService.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Signature get(@PathVariable Long id){
        return signatureService.findById(id);
    }


    // Verify Otp
    @GetMapping("/verify/otp/{otp}")
    public ResponseEntity<?> verifyOtp(@PathVariable String otp,
                                             @RequestHeader(value = "Authorization", required = false)
                                             String authorization
    ) throws Exception{
      Signature signature =  signatureService.findSignatureByOtp(otp);
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
                .amount(signature.getSignaturePlan().getPrice())
                .description("خرید سرویس امضای " + signature.getSignaturePlan().getTitle())
                .callback_url(
                        serverHost +
                                "/api/v1/signature/service/callback" +
                                "?otp=" + otp +
                                "&token=" + accessToken
                )
                .build();

        ZarinpalPaymentResponse res =
                zarinpalPaymentService.payment(paymentReq);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }



    // Callback
    @GetMapping("/callback")
    public ResponseEntity<?> verify(@RequestParam String otp,
                                    @RequestParam String Authority,
                                    @RequestParam String Status) {
        Signature find = signatureService.findSignatureByOtp(otp);

        // Validate transaction payment
        if (Authority == null || Authority.isBlank()
                    || Status == null || Status.isBlank()
                    || !"OK".equals(Status)) {
                return new ResponseEntity<>("پرداخت ناموفق بود", HttpStatus.BAD_REQUEST);
            }

        boolean payStatus = zarinpalPaymentService.verifyPayment(
                    Authority,
                    find.getSignaturePlan().getPrice()
            );

            if (!payStatus) {
                return new ResponseEntity<>("پرداخت ناموفق بود", HttpStatus.BAD_REQUEST);
            }

            CustomResponseDto res = signatureService.verifySignature(find.getOtp());
            signatureService.sendRequestToSignatureService(find);
            res.setMessage("پرداخت با موفقیت انجام شد");
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);


    }



    @GetMapping("/use/{id}")
    public boolean useSignature(@PathVariable Long id) throws Exception {
        Signature req = signatureService.findById(id);
        boolean result = signatureService.useSignature(req);
        return result;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSignature(@PathVariable Long id) throws Exception {

        CustomResponseDto res = signatureService.deleteSignature(id);

        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @PutMapping("/{id}")
    ResponseEntity<?> updateSignature (
            @PathVariable Long id
            ,
            @Valid @RequestBody SignatureRequestDto req,
            Authentication auth,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) throws Exception {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Access Token ارسال نشده است");
        }


        User user = userService.convertUserFromAuthentication(auth);
        req.setUserId(user.getId());
        Signature signature = signatureService.updateSignature(id,req);


        if (signature != null) {
            CustomResponseDto res =CustomResponseDto.builder()
                    .message("کد تایید به شماره تماس شما فرستاده شد")
                    .details("")
                    .timestamp(PersianDate.now())
                    .build();
            return ResponseEntity.ok(res);
        }


        CustomResponseDto res =CustomResponseDto.builder()
                .message("امضای شما دارای اعتبار میباشد")
                .details("")
                .timestamp(PersianDate.now())
                .build();

        return ResponseEntity.ok(res);
    }

}
