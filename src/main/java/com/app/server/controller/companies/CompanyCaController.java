package com.app.server.controller.companies;

import com.app.server.dto.request.companies.CreateCompanyCaRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.exception.AppForbiddenException;
import com.app.server.model.companies.CompanyCa;
import com.app.server.service.CompanyCaService;
import com.app.server.util.wallet_service_producer.dto.response.WalletResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ca/companies")
public class CompanyCaController {

    private final CompanyCaService caService;


    @GetMapping
    public ResponseEntity<?> list(){
        List<CompanyCa> list = caService.companyCaList();
      return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateCompanyCaRequestDto req){
        try{
            CompanyCa res = caService.createCa(req);

            return new ResponseEntity<>(res, HttpStatus.OK);

        } catch (AppForbiddenException e) {
            throw new AppForbiddenException(e.getMessage());
        }

    }


    @GetMapping("/{slug}")
    public CompanyCa getBySlug(@PathVariable String slug){
        return caService.findCompanyCaBySlug(slug);
    }




    @DeleteMapping("/{slug}")
    public CustomResponseDto deleteCompanyCa(@PathVariable String slug){
        return caService.deleteCompanyCaBySlug(slug);
    }


    @PatchMapping("/{slug}/active")
    public CompanyCa setActiveCompanyCa(@PathVariable String slug,@RequestParam boolean value){
        return caService.setActiveBySlug(slug,value);
    }

    @PatchMapping("/{slug}/status")
    public CompanyCa setStatus(@PathVariable String slug,@RequestParam String msg){
        return caService.setStatusBySlug(slug,msg);
    }





    @PostMapping("/{slug}/payment")
    public WalletResponseDto payment(@PathVariable String slug, @AuthenticationPrincipal Jwt auth){
     return caService.payment(slug,auth.getTokenValue());
    }





    @GetMapping("/{caSlug}/callback")
    public Object callback(@PathVariable String caSlug , @RequestParam String sub,
                                       @RequestParam Integer slug , @RequestParam String gateway ,
                                       @RequestParam String Authority , @RequestParam String Status ){

        System.out.println("Companies Ca Slug is -> \s " + caSlug);
        System.out.println("User sub Sub is -> \s " + sub);
        System.out.println("Transaction Slug is -> \s " + slug);
        System.out.println("Gateway Payment  is -> \s " + gateway);
        System.out.println("Authority  is -> \s " + Authority);
        System.out.println("Payment Status  is -> \s " + Status);


        return Status;
    }



}
