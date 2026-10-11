package com.app.server.service.impliment.Company;

import com.app.server.dto.request.CompaniesCaRequestDto;
import com.app.server.exception.AppForbiddenException;
import com.app.server.model.CompaniesCa;
import com.app.server.model.Company;
import com.app.server.model.CompanySubscriptionPlan;
import com.app.server.repository.CompaniesCaRepository;
import com.app.server.service.CompaniesCaService;
import com.app.server.service.CompanyService;
import com.app.server.service.CompanySubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
@Slf4j
public class CompaniesCaServiceImpl implements CompaniesCaService {

    private final CompanyService companyService;
    private final CompanySubscriptionPlanService subscriptionService;


    private final CompaniesCaRepository companiesCaRepository;

    @Override
    public CompaniesCa createCa(CompaniesCaRequestDto req) {

        Company existCompany =  companyService.findCompanyBySlug(req.getCompanySlug());
        CompanySubscriptionPlan subscription = subscriptionService.findCompanySubscriptionPlanBySlug(req.getSubscriptionPlanSlug());

       if(!companyService.isCompanyValidToUse(req.getCompanySlug())) {
           throw new AppForbiddenException("شرکت شما فعال نمیباشد");
       }


       


        CompaniesCa generate = CompaniesCa.builder()
                .status("pending")
                .company(existCompany)
                .subscriptionPlan(subscription)
                .validityDays(subscription.getValidDays())
                .usersCount(subscription.getUsersCount())
                .isActive(false)
                .build();

        return companiesCaRepository.save(generate);
    }

}
