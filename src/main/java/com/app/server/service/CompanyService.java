package com.app.server.service;

import com.app.server.dto.request.CompanyRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.model.Company;

import java.util.List;

public interface CompanyService {

    List<Company> companiesList();
    Company findCompanyBySlug(String slug);
    Company createCompany(CompanyRequestDto req);
    CustomResponseDto deleteCompanyBySlug(String slug);
    Company updateCompanyBySlug(String slug , CompanyRequestDto req);
    Company setActive(String slug , boolean value);
    Company setValid(String slug , boolean value);
    boolean isCompanyValidToUse(String slug);
}
