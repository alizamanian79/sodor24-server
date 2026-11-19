package com.app.server.service;

import com.app.server.dto.request.UserCaRequestDto;
import com.app.server.model.UserCa;

import java.util.List;

public interface UserCaService {

    List<UserCa> list();
    UserCa getUserCaBySlug(String slug);
    UserCa createUserCa(UserCaRequestDto req);
    UserCa updateUserCaBySlug(String slug);
    List<UserCa> myCas(String sub);
}
