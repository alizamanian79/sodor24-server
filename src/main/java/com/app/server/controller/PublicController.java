package com.app.server.controller;

import com.app.server.model.SignaturePlan;
import com.app.server.service.SignaturePlanService;
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
    private final WalletRMQProducer walletRMQProducer;


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

    @GetMapping("/test")
    public ResponseEntity<?> hello(){
        return new ResponseEntity<>("Hello World!", HttpStatus.OK);
    }

    @PostMapping("/test/signature")
    public Object sign(@RequestBody RMQSignatureRequestDto req){
       Object res = signatureRMQProducer.sendAndReceive(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }



    @PostMapping(
            value = "/test/contract",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> signContract(@ModelAttribute RMQContractRequestDto req) {
        RMQContractResponse res = contractRMQProducer.sendAndReceive(req);
        return ResponseEntity.ok(res);
    }



    @GetMapping("/wallet")
    public ResponseEntity<?> wallet() {


            CreateWalletRequestDto req = CreateWalletRequestDto.builder()
                    .sub("")
                    .balance(BigDecimal.ZERO)
                    .currency("IRT")
                    .build();
            WalletResponseDto res = walletRMQProducer.createWallet(req);
            Map<String,Object> data = (Map<String, Object>) res.getData();
            String sub = data.get("sub").toString();




        return new ResponseEntity<>(sub,HttpStatus.OK);
    }



}
