package com.app.server.service.impliment.Company;

import com.app.server.dto.request.CompanyRequestDto;
import com.app.server.dto.response.Sodor24ResponseDto;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.Company;
import com.app.server.model.User;
import com.app.server.repository.CompanyRepository;
import com.app.server.service.CompanyService;
import com.app.server.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Collections;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;


@Slf4j
@RequiredArgsConstructor
@Service
public class CompanyServiceImpl implements CompanyService {

    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;
    private final UserService userService;


    @Override
    public List<Company> companiesList() {
        List<Company> companies = companyRepository.findAll();
        Collections.reverse(companies);
        return companies;
    }

    @Override
    public Company findCompanyBySlug(String slug) {
        return companyRepository.findCompaniesBySlug(slug)
                .orElseThrow(() -> new AppNotFoundException("شرکت پیدا نشد"));
    }

    @Override
    public Company createCompany(CompanyRequestDto req) {

        User user = userService.findUserBySub(req.getUserSub());

        Company createCompany= Company.builder()
                .companyName(req.getCompanyName())
                .privateKeyPassword(req.getPrivateKeyPassword())
                .country(req.getCountry())
                .state(req.getState())
                .location(req.getLocation())
                .description(req.getDescription())
                .completeDescriptions(req.getCompleteDescriptions())
                .isActive(false)
                .isValid(false)
                .owners(Set.of(user))
                .privateKeyPassword(passwordEncoder.encode(req.getPrivateKeyPassword()))
                .build();

        return companyRepository.save(createCompany);
    }


    @Override
    public Sodor24ResponseDto deleteCompanyBySlug(String slug) {
    Company exist= findCompanyBySlug(slug);
    companyRepository.delete(exist);

    return Sodor24ResponseDto.builder()
            .message("شرکت با موفقیت حذف گردید")
            .details("")
            .redirect("/")
            .data("")
            .status(201)
            .build();
    }


    @Transactional
    @Override
    public Company updateCompanyBySlug(String slug , CompanyRequestDto req) {

        Company exist = findCompanyBySlug(slug);
        exist.setCompanyName(req.getCompanyName());
        exist.setCountry(req.getCountry());
        exist.setState(req.getState());
        exist.setDescription(req.getDescription());
        exist.setCompleteDescriptions(req.getCompleteDescriptions());

        Company res = companyRepository.save(exist);


        return res;
    }





    @Transactional
    @Override
    public Company setActive(String slug, boolean value) {
       Company existCompany = findCompanyBySlug(slug);
       existCompany.setActive(value);
       companyRepository.save(existCompany);
        return existCompany;
    }


    @Transactional
    @Override
    public Company setValid(String slug, boolean value) {
        Company existCompany = findCompanyBySlug(slug);
        existCompany.setValid(value);
        companyRepository.save(existCompany);
        return existCompany;
    }


    



}
