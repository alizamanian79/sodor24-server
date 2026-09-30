package com.app.server.service.impliment.Company;

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
    public Company createCompany(Company company,String userSub) {
        User user = userService.findUserBySub(userSub);
        company.setOwners((Set<User>) user);
        return   companyRepository.save(company);
    }

}
