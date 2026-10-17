package com.app.server.controller.companies;

import com.app.server.dto.request.companies.CreateCompanyCaRequestDto;
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
    public ResponseEntity<?> requestToCreate(@Valid @RequestBody CreateCompanyCaRequestDto req){
        try{
            CompanyCa res = caService.createCa(req);

            return new ResponseEntity<>(res, HttpStatus.OK);

        } catch (AppForbiddenException e) {
            throw new AppForbiddenException(e.getMessage());
        }

    }

}
