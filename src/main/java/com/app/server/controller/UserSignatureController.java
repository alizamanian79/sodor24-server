package com.app.server.controller;

import com.app.server.dto.request.SignatureRequest;
import com.app.server.dto.signatureMicroServiceDto.SignatureRequestDto;
import com.app.server.dto.signatureMicroServiceDto.SignatureResponseDto;
import com.app.server.model.UserSignature;
import com.app.server.service.UserSignatureService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/service/signature")
public class UserSignatureController {

    @Autowired
    private RestTemplate restTemplate ;

    private final UserSignatureService userSignatureService;




    @PostMapping
    public UserSignature buySignature(@RequestBody SignatureRequest req){
    return userSignatureService.generateUserSignature(req);
    }





    @GetMapping
    public List<UserSignature> list(){
        return userSignatureService.findAll();
    }

    @GetMapping("/{id}")
    public UserSignature get(@PathVariable Long id){
        return userSignatureService.findById(id);
    }

    @GetMapping("/verify")
    public Object get(@RequestParam String otp){
        return userSignatureService.verifySignature(otp);
    }


    @GetMapping("/test")
    public Object buy() {

        String url = "http://localhost:8585/api/v1/signature/generate";
        SignatureRequestDto req = SignatureRequestDto.builder()
                .city("Tehran")
                .country("Iran")
                .state("Tehran")
                .department("FLoar B")
                .title("Alibaba.ir")
                .valid(true)
                .email("zamanian@gmail.com") // اگر فیلد ایمیل دارید
                .location("Iran")
                .usageCount(5)
                .reason("test")
                .username("ali zamanian")
                .signatureExpired(10)
                .signaturePassword("1234")
                .build();

        ResponseEntity<SignatureResponseDto> response =
                restTemplate.postForEntity(url, req, SignatureResponseDto.class);
        SignatureResponseDto res = response.getBody();

        return res;
    }



}
