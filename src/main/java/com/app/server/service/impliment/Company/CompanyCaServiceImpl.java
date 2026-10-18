package com.app.server.service.impliment.Company;

import com.app.server.dto.request.companies.CompanyCaUpdateDto;
import com.app.server.dto.request.companies.CreateCompanyCaRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.exception.AppForbiddenException;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.companies.CompanyCa;
import com.app.server.model.companies.Company;
import com.app.server.model.companies.CompanySubscriptionPlan;
import com.app.server.repository.companies.CompanyCaRepository;
import com.app.server.service.CompanyCaService;
import com.app.server.service.CompanyService;
import com.app.server.service.CompanySubscriptionPlanService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
@Slf4j
public class CompanyCaServiceImpl implements CompanyCaService {

    private final CompanyService companyService;
    private final CompanySubscriptionPlanService subscriptionService;
    private final CompanyCaRepository companyCaRepository;



    @Override
    public List<CompanyCa> companyCaList() {
        List<CompanyCa> list = companyCaRepository.findAll();
    return list;
    }


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
                .signaturesCount(subscription.getSignaturesCount())
                .build();

        return companyCaRepository.save(generate);
    }


    @Override
    public CompanyCa findCompanyCaBySlug(String slug){
     return companyCaRepository.findCompanyCaBySlug(slug).orElseThrow(()-> new AppNotFoundException("ca شرکت مورد نظر پیدا نشد"));
    }

    @Transactional
    @Override
    public CompanyCa updatedCompanyCa(CompanyCaUpdateDto req) {
        CompanyCa find = findCompanyCaBySlug(req.getSlug());
        find.setSignaturesCount(req.getSignaturesCount());
        find.setUsersCount(req.getUsersCount());
        find.setValidityDays(req.getValidityDays());
        return companyCaRepository.save(find);
    }

    @Override
    public CustomResponseDto deleteCompanyCaBySlug(String slug) {
        CompanyCa find = findCompanyCaBySlug(slug);
        companyCaRepository.delete(find);

        CustomResponseDto<Object> res = CustomResponseDto.builder()
                .status(200)
                .message("اشتراک حذف شد")
                .build();
        return res;
    }

    @Transactional
    @Override
    public CompanyCa setActiveBySlug(String slug,boolean value) {
        CompanyCa find = findCompanyCaBySlug(slug);
        find.setActive(value);
        return companyCaRepository.save(find);
    }


    @Transactional
    @Override
    public CompanyCa setStatusBySlug(String slug,String status) {
        CompanyCa find = findCompanyCaBySlug(slug);
        find.setStatus(status);
        return companyCaRepository.save(find);
    }


}
