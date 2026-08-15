package com.app.server.controller;

import com.app.server.dto.request.LoginRequestDto;
import com.app.server.model.SignaturePlan;
import com.app.server.service.SignaturePlanService;
import com.app.server.service.impliment.NiazpardazSMSService;
import com.app.server.util.ExternalRequest.ExternalRequest;
import com.app.server.util.ExternalRequest.dto.ExternalRequestDto;
import com.app.server.util.ExternalRequest.dto.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public")
public class PublicController {

    private final SignaturePlanService signaturePlanService;

    private final NiazpardazSMSService smsService;
    private final ExternalRequest externalRequest;


    @GetMapping("/signature/plan")
    public Page<SignaturePlan> getSignatures(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return signaturePlanService.getPageableSignaturesPlan(page, size, search, sortBy, sortDir);
    }


    @GetMapping("/signature/plan/{id}")
    public SignaturePlan getSignaturePlan(@PathVariable Long id){
        return signaturePlanService.findSignaturePlanById(id);
    }



//    @GetMapping("/sms/send")
//    public String sendsms() throws Exception{
//        try {
//            return smsService.sendSms("09917403980","منتظرت بودم نبودی ...  از این که سرویس زیلان رو انتخاب کردی سپاس پ");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

//    }




}
