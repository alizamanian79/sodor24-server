package com.app.server.service.impliment.Company;

import com.app.server.dto.request.companies.CreateCompanyCaRequestDto;
import com.app.server.exception.AppForbiddenException;
import com.app.server.model.companies.CompanyCa;
import com.app.server.model.companies.Company;
import com.app.server.model.companies.CompanySubscriptionPlan;
import com.app.server.repository.companies.CompanyCaRepository;
import com.app.server.service.CompanyCaService;
import com.app.server.service.CompanyService;
import com.app.server.service.CompanySubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
@Slf4j
public class CompanyCaServiceImpl implements CompanyCaService {

    private final CompanyService companyService;
    private final CompanySubscriptionPlanService subscriptionService;


    private final CompanyCaRepository companyCaRepository;

    @Override
    public CompanyCa createCa(CreateCompanyCaRequestDto req) {

        Company existCompany =  companyService.findCompanyBySlug(req.getCompanySlug());
        CompanySubscriptionPlan subscription = subscriptionService.findCompanySubscriptionPlanBySlug(req.getSubscriptionPlanSlug());

       if(!companyService.isCompanyValidToUse(req.getCompanySlug())) {
           throw new AppForbiddenException("شرکت شما فعال نمیباشد");
       }


       


        CompanyCa generate = CompanyCa.builder()
                .status("pending")
                .company(existCompany)
                .subscriptionPlan(subscription)
                .validityDays(subscription.getValidDays())
                .usersCount(subscription.getUsersCount())
                .isActive(false)
                .build();

        return companyCaRepository.save(generate);
    }

}
