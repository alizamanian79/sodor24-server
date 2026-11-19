package com.app.server.service.impliment;

import com.app.server.dto.request.UserCaRequestDto;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.User;
import com.app.server.model.UserCa;
import com.app.server.model.companies.CompanyCa;
import com.app.server.repository.UserCaRepository;
import com.app.server.service.CompanyCaService;
import com.app.server.service.UserCaService;
import com.app.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCaServiceImpl implements UserCaService {

    private final UserCaRepository userCaRepository;
    private final PasswordEncoder passwordEncoder;

    private final UserService userService;
    private final CompanyCaService companyCaService;

    @Override
    public List<UserCa> list() {
        return userCaRepository.findAll();
    }

    @Override
    public UserCa getUserCaBySlug(String slug) {
        UserCa existUserCa = userCaRepository.getUserCaBySlug(slug)
                .orElseThrow(()->new AppNotFoundException("گواهی کاربر با این شناسه یافت نشد"));
        return existUserCa;
    }


    @Override
    public List<UserCa> myCas(String sub) {
       List<UserCa> myCas = userCaRepository.getUserCaByUser_Sub(sub);
       return myCas;
    }




    @Override
    public UserCa createUserCa(UserCaRequestDto req) {

        User user = userService.getUserBySlug(req.getUserSlug());
        CompanyCa companyCa = companyCaService.findCompanyCaBySlug(req.getCompanyCaSlug());

        UserCa createUserCa = UserCa.builder()
                .companyCa(companyCa)
                .user(user)
                .caId("die")
                .privateKeyPassword(passwordEncoder.encode(req.getPrivateKeyPassword()))
                .build();
      return   userCaRepository.save(createUserCa);

    }

    @Override
    public UserCa updateUserCaBySlug(String slug) {
        return null;
    }
}
