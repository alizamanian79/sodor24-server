package com.app.server.controller;

import com.app.server.model.UserSignature;
import com.app.server.service.UserSignatureService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/service/signature")
public class UserSignatureController {

    private final UserSignatureService userSignatureService;

    @PostMapping
    public UserSignature buySignature(@RequestParam Long userId,@RequestParam Long signatureId){
    return userSignatureService.generateUserSignature(userId,signatureId);
    }

    @GetMapping
    public List<UserSignature> list(){
        return userSignatureService.findAll();
    }

    @GetMapping("/{id}")
    public UserSignature get(@PathVariable Long id){
        return userSignatureService.findById(id);
    }

}
