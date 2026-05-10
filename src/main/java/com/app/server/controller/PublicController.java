package com.app.server.controller;

import com.app.server.model.SignaturePlan;
import com.app.server.service.SignaturePlanService;
import com.app.server.util.rabbitMQ.ContractRMQProducer;
import com.app.server.util.rabbitMQ.dto.request.RMQContractRequestDto;
import com.app.server.util.rabbitMQ.dto.request.RMQSignatureRequestDto;
import com.app.server.util.rabbitMQ.SignatureRMQProducer;
import com.app.server.util.rabbitMQ.dto.response.RMQContractResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public")
public class PublicController {

    private final SignaturePlanService signaturePlanService;
    private final SignatureRMQProducer signatureRMQProducer;
    private final ContractRMQProducer contractRMQProducer;


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

}
