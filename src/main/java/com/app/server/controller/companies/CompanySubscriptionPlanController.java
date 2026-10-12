package com.app.server.controller.companies;

import com.app.server.dto.request.companies.CreateCompanySubscriptionPlanRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.model.companies.CompanySubscriptionPlan;
import com.app.server.service.CompanySubscriptionPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<?> create(
            @Valid @RequestBody CreateCompanySubscriptionPlanRequestDto request,
            Authentication authentication
    ) {

        CompanySubscriptionPlan created =
                service.createCompanySubscriptionPlan(
                        request,
                        authentication.getName()
                );

//        CustomResponseDto res = CustomResponseDto.builder()
//                .data(created)
//                .message("پلن اشتراک شرکت ساخته شد")
//                .status(200)
//                .build();

        return ResponseEntity
                .status(200)
                .body(created);
    }


    @PutMapping("/{slug}")
    public ResponseEntity<?> update(
            @PathVariable String slug,
            @Valid @RequestBody CreateCompanySubscriptionPlanRequestDto request
    ) {

        CompanySubscriptionPlan updated =
                service.updateCompanySubscriptionPlanBySlug(
                        slug,
                        request
                );


//        CustomResponseDto res = CustomResponseDto.builder()
//                .data(updated)
//                .message("پلن اشتراک شرکت بروزرسانی شد")
//                .status(200)
//                .build();

        return ResponseEntity
                .status(200)
                .body(updated);

    }


    @GetMapping("/active")
    public ResponseEntity<?> findActivePlans() {

        List<CompanySubscriptionPlan> plans =
                service.findActivePlans();
        return ResponseEntity.ok(plans);
    }


    @GetMapping
    public ResponseEntity<?> findAll() {
        List<CompanySubscriptionPlan> plans =
                service.findAll();
        return ResponseEntity.ok(plans);
    }



    @GetMapping("/{slug}")
    public ResponseEntity<?> findBySlug(
            @PathVariable String slug
    ) {

        CompanySubscriptionPlan plan =
                service.findCompanySubscriptionPlanBySlug(slug);


        return ResponseEntity.ok(plan);
    }


    @PatchMapping("/{slug}/active")
    public ResponseEntity<?> setActive(
            @PathVariable String slug,
            @RequestParam boolean value
    ) {

        CompanySubscriptionPlan updated =
                service.setActive(slug, value);

//        CustomResponseDto res = CustomResponseDto.builder()
//                .data(updated)
//                .message("پلن اشتراک شرکت بروزرسانی شد")
//                .status(200)
//                .build();

        return ResponseEntity
                .status(200)
                .body(updated);
    }


    @DeleteMapping("/{slug}")
    public ResponseEntity<?> delete(
            @PathVariable String slug
    ) {

        service.delete(slug);

        CustomResponseDto res = CustomResponseDto.builder()
                .data("")
                .message("پلن اشتراک شرکت با موفقیت حذف شد")
                .status(200)
                .build();

        return ResponseEntity
                .status(res.getStatus())
                .body(res);
    }
}