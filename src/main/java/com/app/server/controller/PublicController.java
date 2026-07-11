package com.app.server.controller;

import com.app.server.model.SignaturePlan;
import com.app.server.service.SignaturePlanService;
import com.app.server.service.impliment.NiazpardazSMSService;
import com.app.server.util.signature_service_producer.ContractRMQProducer;
import com.app.server.util.signature_service_producer.dto.request.RMQContractRequestDto;
import com.app.server.util.signature_service_producer.dto.request.RMQSignatureRequestDto;
import com.app.server.util.signature_service_producer.SignatureRMQProducer;
import com.app.server.util.signature_service_producer.dto.response.RMQContractResponse;
import com.app.server.util.wallet_service_producer.WalletRMQProducer;
import com.app.server.util.wallet_service_producer.dto.request.CreateWalletRequestDto;
import com.app.server.util.wallet_service_producer.dto.response.WalletResponseDto;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Filter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public")
public class PublicController {

    private final SignaturePlanService signaturePlanService;
    private final SignatureRMQProducer signatureRMQProducer;
    private final ContractRMQProducer contractRMQProducer;

    private final NiazpardazSMSService smsService;


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




    @GetMapping("/sms/send")
    public String sendsms() throws Exception{
        try {
            return smsService.sendSms("09917403979","salam");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }




}
