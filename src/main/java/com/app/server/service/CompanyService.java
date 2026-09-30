package com.app.server.service;

import com.app.server.model.Company;

import java.util.Map;

public interface CompanyService {

    Company createCompany(Company company,String userSub);

}
