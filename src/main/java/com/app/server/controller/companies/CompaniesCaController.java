package com.app.server.controller.companies;

import com.app.server.dto.request.CompaniesCaRequestDto;
import com.app.server.model.CompaniesCa;
import com.app.server.service.CompaniesCaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ca/companies")
public class CompaniesCaController {

    private final CompaniesCaService caService;

    @PostMapping
    public CompaniesCa requestToCreate(@Valid @RequestBody CompaniesCaRequestDto req){
        return caService.createCa(req);
    }

}
