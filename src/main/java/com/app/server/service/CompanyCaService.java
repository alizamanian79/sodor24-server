package com.app.server.service;

import com.app.server.dto.request.companies.CreateCompanyCaRequestDto;
import com.app.server.model.companies.CompanyCa;

public interface CompanyCaService {

    CompanyCa createCa(CreateCompanyCaRequestDto req);

}
