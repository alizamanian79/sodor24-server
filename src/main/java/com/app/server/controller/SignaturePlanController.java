package com.app.server.controller;
import com.app.server.dto.request.SignaturePlanRequestDto;
import com.app.server.model.SignaturePlan;
import com.app.server.model.User;
import com.app.server.service.SignaturePlanService;
import com.app.server.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/signature/plan")
@RequiredArgsConstructor
public class SignaturePlanController {

    private final SignaturePlanService signaturePlanService;
    private final UserService userService;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<SignaturePlan> getAllSignaturePlans() {
        return signaturePlanService.getAllSignaturePlans();
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public SignaturePlan getSignaturePlanById(@PathVariable Long id) {
        return signaturePlanService.findSignaturePlanById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public SignaturePlan generateSignaturePlan(
            @Valid @RequestBody SignaturePlanRequestDto req,Authentication auth) {

        User user = userService.convertUserFromAuthentication(auth);
        req.setCreatorId(user.getId());

        return signaturePlanService.generateSignaturePlan(req);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public SignaturePlan updateSignaturePlan(
            @PathVariable Long id,
            @Valid @RequestBody SignaturePlanRequestDto req,Authentication authentication
    ) {

        User user = userService.convertUserFromAuthentication(authentication);
        req.setCreatorId(user.getId());

        req.setUpdatedUserId(user.getId());
        return signaturePlanService.updateSignaturePlanById(req, id);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public Object deleteSignaturePlan(@PathVariable Long id) {
       return signaturePlanService.deleteSignaturePlan(id);
    }




    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/active/set")
    public Object changeActiveSignaturePlan(@RequestParam Long id, @RequestParam boolean active) {
        return signaturePlanService.activeSignaturePlan(id, active);
    }



}
