package com.app.server.controller;

import com.app.server.dto.request.SignatureRequest;
import com.app.server.dto.signatureDto.SignatureRequestDto;
import com.app.server.dto.signatureDto.SignatureResponseDto;
import com.app.server.model.UserSignature;
import com.app.server.service.UserSignatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/service/signature")
public class UserSignatureController {



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

    @PostMapping("/verify")
    public Object verify(@RequestParam String otp){
            return userSignatureService.verifySignature(otp);
    }


//    @PostMapping("/test")
//    public SignatureResponseDto test(@RequestBody SignatureRequestDto req) {
//        SignatureResponseDto res = userSignatureService.sendSignatureRequest(req);
//        return res;
//    }



}
