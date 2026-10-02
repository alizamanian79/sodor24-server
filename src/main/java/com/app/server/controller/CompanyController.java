package com.app.server.controller;

import com.app.server.dto.request.CreateCompanyRequestDto;
import com.app.server.model.Company;
import com.app.server.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/create")
    public ResponseEntity<?> createCompany(
          @Valid @RequestBody CreateCompanyRequestDto req,
          Authentication auth
    ) {

        req.setUserSub(auth.getName().toString());
       Company res = companyService.createCompany(req);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}