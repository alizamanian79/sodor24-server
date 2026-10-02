package com.app.server.controller;

import com.app.server.dto.response.Sodor24ResponseDto;
import com.app.server.model.CompanySubscriptionPlan;
import com.app.server.service.CompanySubscriptionPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/company-subscription-plans")
@RequiredArgsConstructor
public class CompanySubscriptionPlanController {

    private final CompanySubscriptionPlanService service;


    @PostMapping
    public ResponseEntity<CompanySubscriptionPlan> create(
            @Valid @RequestBody CompanySubscriptionPlan request,
            Authentication auth
    ) {

        CompanySubscriptionPlan created =
                service.createCompanySubscriptionPlan(request,auth.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }


    @PutMapping("/{slug}")
    public ResponseEntity<CompanySubscriptionPlan> update(
            @PathVariable String slug,
            @Valid @RequestBody CompanySubscriptionPlan request
    ) {

        CompanySubscriptionPlan updated =
                service.updateCompanySubscriptionPlanBySlug(slug, request);

        return ResponseEntity.ok(updated);
    }


    @GetMapping("/{slug}")
    public ResponseEntity<CompanySubscriptionPlan> findById(
            @PathVariable String slug
    ) {

        return ResponseEntity.ok(
                service.findCompanySubscriptionPlanBySlug(slug)
        );
    }


    @GetMapping
    public ResponseEntity<List<CompanySubscriptionPlan>> findAll() {

        return ResponseEntity.ok(
                service.findAll()
        );
    }


    @GetMapping("/active")
    public ResponseEntity<List<CompanySubscriptionPlan>> findActivePlans() {

        return ResponseEntity.ok(
                service.findActivePlans()
        );
    }


    @DeleteMapping("/{slug}")
    public  Sodor24ResponseDto delete(
            @PathVariable String slug
    ) {

        Sodor24ResponseDto res = service.delete(slug);

        return res;
    }



    @PatchMapping("/{slug}")
    public CompanySubscriptionPlan setActive(
            @PathVariable String slug,
            @RequestParam boolean value
    ) {

        return service.setActive(slug, value);
    }


    @GetMapping("/{slug}/status")
    public boolean getStatus(
            @PathVariable String slug
    ) {
        return service.checkActivePlan(slug);
    }


}