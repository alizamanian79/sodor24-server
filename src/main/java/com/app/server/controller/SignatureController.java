package com.app.server.controller;
import com.app.server.model.Signature;
import com.app.server.service.SignatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/signature")
@RequiredArgsConstructor
public class SignatureController {

    private final SignatureService signatureService;


    @GetMapping
    public List<Signature> getAllSignatures() {
        return signatureService.getSignatures();
    }


    @GetMapping("/{id}")
    public Signature getSignatureById(@PathVariable Long id) {
        return signatureService.findSignatureById(id);
    }

    @PostMapping
    public Signature generateSignature(@RequestBody Signature signature) {
        return signatureService.generateSignature(signature);
    }

    @PutMapping
    public Signature updateSignature(@RequestBody Signature signature) {
        return signatureService.updateSignature(signature);
    }

    @DeleteMapping("/{id}")
    public Object deleteSignature(@PathVariable Long id) {
       return signatureService.deleteSignature(id);
    }

    @PutMapping("/active/set")
    public Object deleteSignature(@RequestParam Long id, @RequestParam boolean active) {
        return signatureService.activeSignature(id, active);
    }

}
