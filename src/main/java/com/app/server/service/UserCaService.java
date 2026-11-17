package com.app.server.service;

import com.app.server.dto.request.UserCaRequestDto;
import com.app.server.model.UserCa;

public interface UserCaService {

    UserCa createUserCa(UserCaRequestDto req);

}
