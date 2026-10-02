package com.app.server.service.impliment.Company;

import com.app.server.dto.request.CreateCompanyRequestDto;
import com.app.server.model.Company;
import com.app.server.model.User;
import com.app.server.repository.CompanyRepository;
import com.app.server.service.CompanyService;
import com.app.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;


@Slf4j
@RequiredArgsConstructor
@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserService userService;



    @Override
    public Company createCompany(CreateCompanyRequestDto req) {

        User user = userService.findUserBySub(req.getUserSub());


        Company createCompany= Company.builder()
                .companyName(req.getCompanyName())
                .validityDays(req.getValidityDays())
                .privateKeyPassword(req.getPrivateKeyPassword())
                .country(req.getCountry())
                .state(req.getState())
                .location(req.getLocation())
                .owners(Set.of(user))
                .build();

        return   companyRepository.save(createCompany);
    }

}
