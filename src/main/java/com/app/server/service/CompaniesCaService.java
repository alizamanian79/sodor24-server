package com.app.server.service;

import com.app.server.dto.request.CompaniesCaRequestDto;
import com.app.server.model.CompaniesCa;

public interface CompaniesCaService {

    CompaniesCa createCa(CompaniesCaRequestDto req);

}
