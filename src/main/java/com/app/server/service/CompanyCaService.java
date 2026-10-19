package com.app.server.service;

import com.app.server.dto.request.companies.CreateCompanyCaRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.model.companies.CompanyCa;

import java.util.List;

public interface CompanyCaService {

    List<CompanyCa> companyCaList();
    CompanyCa createCa(CreateCompanyCaRequestDto req);
    CompanyCa findCompanyCaBySlug(String slug);
    CustomResponseDto deleteCompanyCaBySlug(String slug);
    CompanyCa setActiveBySlug(String slug,boolean value);
    CompanyCa setStatusBySlug(String slug,String status);




}
