package com.app.server.service;

import com.app.server.dto.request.CreateCompanyRequestDto;
import com.app.server.model.Company;

import java.util.Map;

public interface CompanyService {

    Company createCompany(CreateCompanyRequestDto req);

}
