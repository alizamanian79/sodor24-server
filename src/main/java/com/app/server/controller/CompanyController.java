package com.app.server.controller;

import com.app.server.dto.request.CompanyRequestDto;
import com.app.server.dto.response.Sodor24ResponseDto;
import com.app.server.exception.AppForbiddenException;
import com.app.server.model.Company;
import com.app.server.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;


    @GetMapping
    public ResponseEntity<?> companyList() {
        List<Company> res = companyService.companiesList();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<?> createCompany(
          @Valid @RequestBody CompanyRequestDto req,
          Authentication auth
    ) {
        req.setUserSub(auth.getName().toString());
       Company res = companyService.createCompany(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }




    @DeleteMapping("/{slug}")
    public ResponseEntity<?> deleteCompany(
            @Valid @PathVariable String slug
    ) {
        Sodor24ResponseDto res = companyService.deleteCompanyBySlug(slug);
        return new ResponseEntity<>(res, HttpStatus.valueOf(res.getStatus()));
    }


    @GetMapping("/{slug}")
    public ResponseEntity<?> findCompanyBySlug(
            @Valid @PathVariable String slug
    ) {
        Company res = companyService.findCompanyBySlug(slug);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }


    @PutMapping("/{slug}")
    public ResponseEntity<?> updatedCompanies(
            @Valid @PathVariable String slug,
            @Valid @RequestBody CompanyRequestDto req
    ) {
        Company res = companyService.updateCompanyBySlug(slug , req);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }


    @PatchMapping("/active/{slug}")
    public ResponseEntity<?> setActive(
            @Valid @PathVariable String slug,
            @Valid @RequestParam boolean value
    ) {
        Company res = companyService.setActive(slug , value);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }



    @PatchMapping("/valid/{slug}")
    public ResponseEntity<?> setValid(
            @Valid @PathVariable String slug,
            @Valid @RequestParam boolean value
    ) {
        Company res = companyService.setValid(slug , value);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }


    @GetMapping("/{slug}/isvalid")
    public ResponseEntity<?> isCompanyValidToUse(
            @PathVariable String slug
    ) {
        boolean result = companyService.isCompanyValidToUse(slug);
        return ResponseEntity.ok(result);
    }


}