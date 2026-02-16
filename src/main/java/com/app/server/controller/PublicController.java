package com.app.server.controller;

import com.app.server.model.Signature;
import com.app.server.service.SignatureService;
import com.app.server.util.rabbitMQ.ContractRMQProducer;
import com.app.server.util.rabbitMQ.dto.request.RMQContractRequestDto;
import com.app.server.util.rabbitMQ.dto.request.RMQSignatureRequestDto;
import com.app.server.util.rabbitMQ.SignatureRMQProducer;
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

    private final SignatureService signatureService;
    private final SignatureRMQProducer signatureRMQProducer;
    private final ContractRMQProducer contractRMQProducer;


    @GetMapping("/signatures")
    public Page<Signature> getSignatures(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return signatureService.getPageableSignatures(page, size, search, sortBy, sortDir);
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
    public ResponseEntity<?> signContract(
            @RequestPart("file") MultipartFile pdfFile,
            @RequestPart("privateKeyFile") MultipartFile privateKeyFile,
            @RequestParam("keyPassword") String keyPassword,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) String country
    ) {

        RMQContractRequestDto req = new RMQContractRequestDto();
        req.setFile(pdfFile);
        req.setPrivateKeyFile(privateKeyFile);
        req.setKeyPassword(keyPassword);
        req.setReason(reason);
        req.setCountry(country);

        Object res = contractRMQProducer.sendAndReceive(req);

        return ResponseEntity.ok(res);
    }

}
