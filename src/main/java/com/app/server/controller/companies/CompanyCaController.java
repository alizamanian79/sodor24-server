package com.app.server.controller.companies;

import com.app.server.dto.request.companies.CompanyCaUpdateDto;
import com.app.server.dto.request.companies.CreateCompanyCaRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.exception.AppForbiddenException;
import com.app.server.model.companies.CompanyCa;
import com.app.server.service.CompanyCaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    @PutMapping("/{slug}")
    public CompanyCa updateCompanyCa(@PathVariable String slug,@Valid @RequestBody CompanyCaUpdateDto req){
        return caService.updatedCompanyCa(slug,req);
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







}
