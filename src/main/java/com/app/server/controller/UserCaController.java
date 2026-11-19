package com.app.server.controller;

import com.app.server.dto.request.UserCaRequestDto;
import com.app.server.model.UserCa;
import com.app.server.service.UserCaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ca/users")
public class UserCaController {

    private final UserCaService userCaService;

    @GetMapping
    public List<UserCa> list(){
        return userCaService.list();
    }

    @GetMapping("/find/{userSub}")
    public UserCa getBySlug(@PathVariable String userSub){
        return userCaService.getUserCaBySlug(userSub);
    }

    @GetMapping("/me")
    public List<UserCa> myCas(@AuthenticationPrincipal Jwt auth){
        return userCaService.myCas(auth.getSubject().toString());
    }



    @PostMapping
    public UserCa create(@RequestBody UserCaRequestDto req){
        return userCaService.createUserCa(req);
    }

}
