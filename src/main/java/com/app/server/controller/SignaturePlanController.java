package com.app.server.controller;
import com.app.server.dto.request.SignaturePlanRequestDto;
import com.app.server.model.SignaturePlan;
import com.app.server.service.SignaturePlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/signature/plan")
@RequiredArgsConstructor
public class SignaturePlanController {

    private final SignaturePlanService signaturePlanService;


    @GetMapping
    public List<SignaturePlan> getAllSignaturePlans() {
        return signaturePlanService.getAllSignaturePlans();
    }


    @GetMapping("/{id}")
    public SignaturePlan getSignaturePlanById(@PathVariable Long id) {
        return signaturePlanService.findSignaturePlanById(id);
    }

    @PostMapping
    public SignaturePlan generateSignaturePlan(@Valid @RequestBody SignaturePlanRequestDto req) {
        return signaturePlanService.generateSignaturePlan(req);
    }

    @PutMapping("/{id}")
    public SignaturePlan updateSignaturePlan(
            @PathVariable Long id,
            @Valid @RequestBody SignaturePlanRequestDto req
    ) {
        return signaturePlanService.updateSignaturePlanById(req, id);
    }



    @DeleteMapping("/{id}")
    public Object deleteSignaturePlan(@PathVariable Long id) {
       return signaturePlanService.deleteSignaturePlan(id);
    }

    @PutMapping("/active/set")
    public Object changeActiveSignaturePlan(@RequestParam Long id, @RequestParam boolean active) {
        return signaturePlanService.activeSignaturePlan(id, active);
    }

}
