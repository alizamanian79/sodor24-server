package com.app.server.controller;

import com.app.server.dto.request.UserCaRequestDto;
import com.app.server.model.UserCa;
import com.app.server.service.UserCaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ca/users")
public class UserCaController {

    private final UserCaService userCaService;

    @PostMapping
    public UserCa create(@RequestBody UserCaRequestDto req){
        return userCaService.createUserCa(req);
    }

}
