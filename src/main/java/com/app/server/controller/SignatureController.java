package com.app.server.controller;

import com.app.server.dto.request.SignatureRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.exception.AppBadRequestException;
import com.app.server.model.Signature;
import com.app.server.model.User;
import com.app.server.service.SignaturePlanService;
import com.app.server.service.SignatureService;
import com.app.server.service.UserService;

import com.app.server.util.signature_service_producer.dto.request.DownloadFileRequestDto;
import com.app.server.util.signature_service_producer.dto.response.DownloadFileResponseDto;
import com.app.server.util.signature_service_producer.producer.DownloadFileProducer;
import com.app.server.util.signature_service_producer.producer.SignatureProducer;
import com.app.server.util.wallet_service_producer.TransactionRMQProducer;
import com.app.server.util.wallet_service_producer.WalletRMQProducer;
import com.app.server.util.wallet_service_producer.dto.request.PaymentRequestDto;
import com.app.server.util.wallet_service_producer.dto.response.WalletResponseDto;
import com.github.mfathi91.time.PersianDate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/signature/service")
public class SignatureController {



    private final SignatureService signatureService;
    private final UserService userService;
    private final TransactionRMQProducer transactionRMQProducer;


    @PostMapping()
     ResponseEntity<?> signatureRequest(
            @Valid @RequestBody SignatureRequestDto req,
            Authentication auth,
            @RequestHeader(value = "Authorization",
                    required = false) String authorization
    ) {

        User user = userService.convertUserFromAuthentication(auth);
        req.setUserId(user.getId());


        Signature signature = signatureService.generateSignature(req);

//        if (signature.isValid()) {
//            return ResponseEntity.ok(
//                    CustomResponseDto.builder()
//                            .message("این امضا خریداری شده است و هنوز اعتبار دارد")
//                            .build()
//            );
//        }

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
//    @GetMapping("/verify/otp/{otp}")
//    public ResponseEntity<?> verifyOtp(@PathVariable String otp) throws Exception{
//      CustomResponseDto res =  signatureService.verifySignature(otp);
//
//        return new ResponseEntity<>(res, HttpStatus.OK);
//    }


    @GetMapping("/buy")
    public ResponseEntity<?> buySignature(
            @RequestParam Long sid,@RequestParam Integer slug) throws Exception{

        CustomResponseDto signatured= new CustomResponseDto();
        Signature existSignature = signatureService.findById(sid);

//        BigDecimal signaturePrice = BigDecimal.valueOf(
//                existSignature.getSignaturePlan().getPrice()
//        );

        WalletResponseDto walletResponse =
                transactionRMQProducer.getTransactionBySlug(slug);

        Map<String, Object> data =
                (Map<String, Object>) walletResponse.getData();


        if (data.get("status").equals("Paid")){
             signatured =  signatureService.generateSignatureKeys(sid);
        }

        return ResponseEntity.status(walletResponse.getStatus()).body(signatured);
    }







    // Call back redirecter
//    @GetMapping("/sandbox/callback")
//    public RedirectView testCallback(@RequestParam String otp) {
//        Signature find = signatureService.findSignatureByOtp(otp);
//
//        try {
//            CustomResponseDto res = signatureService.verifySignature(otp);
//            String queryPrefix = "msg=" + "OK" + "&sid=" + find.getId().toString();
//            return redirectView(find.isValid(), queryPrefix);
//        } catch (Exception e) {
//            String queryPrefix = "msg=" + "ERROR" + "&sid=" + find.getId().toString();
//            return redirectView(find.isValid(), queryPrefix);
//        }
//    }




//    @GetMapping("/verify/callback")
//    public ResponseEntity<?> testCallback(@RequestParam String otp) {
//        Signature find = signatureService.findSignatureByOtp(otp);
//
//        try {
//            CustomResponseDto res = signatureService.verifySignature(otp);
//            return new ResponseEntity<>(res,HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(e,HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }



    public RedirectView redirectView(boolean value, String queryPrefix) {
        String url = value
                ? "http://localhost:3000/transaction/payed?" + queryPrefix
                : "http://localhost:3000/transaction/error?" + queryPrefix;

        RedirectView redirectView = new RedirectView(url);
        redirectView.setStatusCode(HttpStatus.FOUND); // 302
        redirectView.setContextRelative(false); // عدم استفاده از context path
        redirectView.setHttp10Compatible(false); // استفاده از HTTP 1.1
        redirectView.setExposeModelAttributes(false); // عدم expose مدل attributes
        redirectView.setExposePathVariables(false); // عدم expose path variables

        return redirectView;
    }



//    @GetMapping("/use/{id}")
//    public boolean useSignature(@PathVariable Long id) throws Exception {
//        Signature req = signatureService.findById(id);
//        boolean result = signatureService.useSignature(req);
//        return result;
//    }
//

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSignature(@PathVariable Long id) throws Exception {

        CustomResponseDto res = signatureService.deleteSignature(id);

        return new ResponseEntity<>(res,HttpStatus.OK);
    }




    @PreAuthorize("hasRole('ADMIN')")
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
