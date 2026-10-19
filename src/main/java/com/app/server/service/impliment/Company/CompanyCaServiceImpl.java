package com.app.server.service.impliment.Company;

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
import com.app.server.util.signature_service_producer.producer.SignatureProducer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@Service
@Slf4j
public class CompanyCaServiceImpl implements CompanyCaService {

    private final CompanyService companyService;
    private final CompanySubscriptionPlanService subscriptionService;
    private final CompanyCaRepository companyCaRepository;

    public static final String  CACHE_NAME="companyCaCache";


    @Override
    public List<CompanyCa> companyCaList() {
        List<CompanyCa> list = companyCaRepository.findAll();
    return list;
    }


    @Override
    public CompanyCa createCa(CreateCompanyCaRequestDto req) {

        Company company = companyService.findCompanyBySlug(req.getCompanySlug());

        CompanySubscriptionPlan subscription =
                subscriptionService.findCompanySubscriptionPlanBySlug(
                        req.getSubscriptionPlanSlug()
                );

        if (!companyService.isCompanyValidToUse(req.getCompanySlug())) {
            throw new AppForbiddenException("شرکت شما فعال نمی‌باشد");
        }

        CompanyCa companyCa = CompanyCa.builder()
                .status("pending")
                .company(company)
                .subscriptionPlan(subscription)
                .validityDays(subscription.getValidDays())
                .usersCount(subscription.getUsersCount())
                .signaturesCount(subscription.getSignaturesCount())
                .isActive(false)
                .build();

        return companyCaRepository.save(companyCa);
    }


    @Cacheable(value = CACHE_NAME,key = "#slug")
    @Override
    public CompanyCa findCompanyCaBySlug(String slug){
     return companyCaRepository.findCompanyCaBySlug(slug).orElseThrow(()-> new AppNotFoundException("ca شرکت مورد نظر پیدا نشد"));
    }



    @CacheEvict(value = CACHE_NAME,key = "#slug")
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


    @CachePut(value = CACHE_NAME,key = "#slug")
    @Transactional
    @Override
    public CompanyCa setActiveBySlug(String slug,boolean value) {
        CompanyCa find = findCompanyCaBySlug(slug);
        find.setActive(value);
        find.setStatus("paid");
        return companyCaRepository.save(find);
    }


    @CachePut(value = CACHE_NAME,key = "#slug")
    @Transactional
    @Override
    public CompanyCa setStatusBySlug(String slug,String status) {
        CompanyCa find = findCompanyCaBySlug(slug);
        find.setStatus(status);
        return companyCaRepository.save(find);
    }


    // Deleted after 5 minutest
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void deleteExpiredPendingCas() {

        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(5);

        int deletedCount = companyCaRepository.deleteExpiredPendingCas(
                "pending",
                expireTime
        );



        if (deletedCount > 0) {
            log.info("{} expired pending Company CA(s) deleted", deletedCount);
        }
    }








}
