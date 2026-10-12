package com.app.server.controller.companies;

import com.app.server.dto.request.companies.CreateCompanyCaRequestDto;
import com.app.server.model.companies.CompanyCa;
import com.app.server.service.CompanyCaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ca/companies")
public class CompanyCaController {

    private final CompanyCaService caService;

    @PostMapping
    public CompanyCa requestToCreate(@Valid @RequestBody CreateCompanyCaRequestDto req){
        return caService.createCa(req);
    }

}
