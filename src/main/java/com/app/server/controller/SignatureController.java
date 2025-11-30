package com.app.server.controller;

import com.app.server.dto.request.SignatureRequestDto;
import com.app.server.exception.AppConflicException;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.Signature;
import com.app.server.service.SignatureService;
import com.app.server.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@RequestMapping("/api/v1/signature")
@RequiredArgsConstructor
public class SignatureController {

    private final RestTemplate restTemplate;
    private final UserService userService;
    private final SignatureService signatureService;



    @GetMapping
    public List<Signature> signatureList(){
        return signatureService.signatureList();
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getSignatureById(@PathVariable Long id) throws AppNotFoundException {
        try {
            Signature signature = signatureService.getSignatureById(id);
            return new ResponseEntity<>(signature, HttpStatus.OK);
        } catch (RuntimeException e) {
            throw new AppNotFoundException("امضای شما پیدا نشد");
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSignatureById(@PathVariable Long id)
            throws AppNotFoundException {
       try{
           Object res = signatureService.deleteSignatureById(id);
           return new ResponseEntity<>(res, HttpStatus.OK);
       }catch (RuntimeException e){
           throw new AppNotFoundException("امضای شما پیدا نشد");
       }
    }



    @Transactional
    @PostMapping
    public ResponseEntity<?> generateSignature(
            @ModelAttribute SignatureRequestDto req) {
        try{

            Object res = signatureService.generateSignature(req);
            return new ResponseEntity<>(res, HttpStatus.OK);

        } catch (Exception e){
            throw new AppConflicException("کلید شما وجود دارد");
        }
    }


    @GetMapping("/active/{slug}")
    public ResponseEntity<?> activeSignature(@PathVariable String slug) throws JsonProcessingException {
        boolean res = signatureService.activeSignature(slug);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }





    @GetMapping("/find/{slug}")
    public Signature findSignatureBySlug(@PathVariable String slug) {
        return signatureService.findSignatureByIdSlug(slug);
    }


    @GetMapping("/use")
    public ResponseEntity<?> useSignature(@RequestParam String slug ,@RequestParam int count) {
        Object res = signatureService.useSignature(slug,count);
       return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
