package com.app.server.service;

import com.app.server.dto.request.CreateCompanyRequestDto;
import com.app.server.dto.response.Sodor24ResponseDto;
import com.app.server.model.Company;

import java.util.List;
import java.util.Map;

public interface CompanyService {

    List<Company> companiesList();
    Company findCompanyBySlug(String slug);
    Company createCompany(CreateCompanyRequestDto req);
    Sodor24ResponseDto deleteCompanyBySlug(String slug);
    Company updateCompanyBySlug(String slug);

}
