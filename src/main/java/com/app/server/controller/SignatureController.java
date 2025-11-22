package com.app.server.controller;

import com.app.server.dto.response.CustomResponseDto;
import com.app.server.exception.AppConflicException;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.Signature;
import com.app.server.model.User;
import com.app.server.service.SignatureService;
import com.app.server.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mfathi91.time.PersianDate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
            @RequestParam String username,
            @RequestParam String country,
            @RequestParam String reason,
            @RequestParam String location,
            @RequestParam String organization,
            @RequestParam String department,
            @RequestParam String state,
            @RequestParam String city,
            @RequestParam String email,
            @RequestParam String title,
            @RequestParam String signatureExpired,
            @RequestParam String signaturePassword) {
        try{


            User user =userService.findUserByUsername(username);
            String idGenerator="signature-"+user.getUsername()+"-"+UUID.randomUUID().toString();



            String url = "http://localhost:8585/api/v1/signature/generate";
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("username", user.getFullName() !=null ? user.getFullName():"میهمان");
            requestBody.put("country", country);
            requestBody.put("reason", reason);
            requestBody.put("location", location);
            requestBody.put("organization", organization);
            requestBody.put("department", department);
            requestBody.put("state", state);
            requestBody.put("city", city);
            requestBody.put("email", email);
            requestBody.put("title", title);
            requestBody.put("userId",idGenerator);
            requestBody.put("signatureExpired", Integer.parseInt(signatureExpired));
            requestBody.put("signaturePassword", signaturePassword);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> responseBody = mapper.readValue(response.getBody(), Map.class);
            String id = (String) responseBody.get("userId");

            // Saving signature
            Signature signature = Signature.builder()
                    .signatureId(id)
                    .user(user)
                    .isValid(true)
                    .expiredAt(LocalDateTime.now().plusDays(Integer.parseInt(signatureExpired)))
                    .usageCount(5)
                    .price(0L)
                    .build();
           Signature saved = signatureService.generateSignature(user.getId(),signature);


            Map<String, Object> responseBodySignature = mapper.readValue(response.getBody(), Map.class);
            String cert = (String) responseBody.get("cert");
            String private_key = (String) responseBody.get("privateKey");
            String public_key = (String) responseBody.get("publicKey");
            String fullName = (String) responseBody.get("username");

            Map<String, Object> customRes = new HashMap<>();
            customRes.put("signatureInfo", saved);
            customRes.put("cert", cert);
            customRes.put("private_key", private_key);
            customRes.put("public_key", public_key);
            customRes.put("fullName", fullName);

            return new ResponseEntity<>(customRes, HttpStatus.OK);

  }catch (Exception e){
            throw new AppConflicException("کیلید شما وجود دارد");
        }

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
