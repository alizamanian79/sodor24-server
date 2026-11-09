package com.app.server.service.impliment.Company;

import com.app.server.dto.request.companies.CreateCompanyCaRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.dto.response.MeResponseDto;
import com.app.server.exception.AppBadRequestException;
import com.app.server.exception.AppConflicException;
import com.app.server.exception.AppForbiddenException;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.User;
import com.app.server.model.companies.CompanyCa;
import com.app.server.model.companies.Company;
import com.app.server.model.companies.CompanySubscriptionPlan;
import com.app.server.repository.companies.CompanyCaRepository;
import com.app.server.service.CompanyCaService;
import com.app.server.service.CompanyService;
import com.app.server.service.CompanySubscriptionPlanService;
import com.app.server.service.UserService;
import com.app.server.util.signature_service_producer.CertificateProducer;
import com.app.server.util.wallet_service_producer.TransactionRMQProducer;
import com.app.server.util.wallet_service_producer.WalletRMQProducer;
import com.app.server.util.wallet_service_producer.dto.request.PaymentRequestDto;
import com.app.server.util.wallet_service_producer.dto.request.PaymentVerifierRequestDto;
import com.app.server.util.wallet_service_producer.dto.response.WalletResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@RequiredArgsConstructor
@Service
@Slf4j
public class CompanyCaServiceImpl implements CompanyCaService {

    private final CompanyService companyService;
    private final CompanySubscriptionPlanService subscriptionService;
    private final CompanyCaRepository companyCaRepository;
    public static final String  CACHE_NAME="companyCaCache";
    private final PasswordEncoder passwordEncoder;
    private final CertificateProducer certificateProducer;
    private final WalletRMQProducer walletRMQProducer;
    private final TransactionRMQProducer transactionRMQProducer;
    private final UserService userService;



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
                .country(company.getCountry())
                .status(company.getState())
                .location(company.getLocation())
                .privateKeyPassword(passwordEncoder.encode(req.getPrivateKeyPassword()))
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
    @Scheduled(fixedDelay = 300_000)
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





    @Override
    public WalletResponseDto payment(String slug, String token) {



        Map<String, Object> user =
                (Map<String, Object>) userService.me(token).getAccountData();


        if (user.get("email").equals(null)){
            throw new AppConflicException("ایمیل خود را وارد نمایید و ان را تایید کنید");
        }



        CompanyCa ca = findCompanyCaBySlug(slug);

        String email = Objects.toString(user.get("email"), "sodor24.ir@gmail.com");
        String phoneNumber = Objects.toString(user.get("phoneNumber"), null);

        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new AppConflicException("شماره تلفن کاربر ثبت نشده است");
        }

        PaymentRequestDto request = PaymentRequestDto.builder()
                .process("withdraw")
                .gateway("zarinpal")
                .sub(user.get("sub").toString())
                .amount(ca.getSubscriptionPlan().getPrice())
                .email(email)
                .description(
                        "خرید سرویس  "+
                                "\s" + ca.getSubscriptionPlan().getTitle() + "\s"

                )
                .phoneNumber(phoneNumber)
                .callbackUrl("http://localhost:8181/api/v1/ca/companies/"+slug+"/callback?")
                .build();

        return walletRMQProducer.paymentRequest(request);
    }



    @Transactional
    @Override
    public  Map<String, Object> callBackCompanyCa(String caSub,String walletSub ,
                                                  boolean value ,
                                                  Integer transactionSlug){

        if (!value){
            throw new AppBadRequestException("مقدار خالی میباشد");
        }

        CompanyCa companyCa = findCompanyCaBySlug(caSub);


        PaymentVerifierRequestDto paymentVerifierRequestDto = PaymentVerifierRequestDto.builder()
                .sub(walletSub)
                .slug(transactionSlug)
                .callbackUrl("https://google.com")
                .gateway("zarinpal")
                .build();
        walletRMQProducer.paymentVerifier(paymentVerifierRequestDto);

        //

        com.app.server.util.signature_service_producer.dto.request.ca.CreateCompanyCaRequestDto
                request = com.app.server.util.signature_service_producer.dto.request.ca.CreateCompanyCaRequestDto.
                builder()
                .companyId(companyCa.getCompany().getCompanyName().toLowerCase())
                .companyName(companyCa.getCompany().getCompanyName().toLowerCase())
                .state(companyCa.getCompany().getState())
                .location(companyCa.getCompany().getLocation())
                .country(companyCa.getCompany().getCountry())
                .validityDays(companyCa.getValidityDays())
                .privateKeyPassword(companyCa.getPrivateKeyPassword())
                .build();

        // After Ca created for company in signature service
        Map<String, Object> data = certificateProducer.createCompanyCa(request);
        companyCa.setCaId(data.get("companyId").toString());
        companyCa.setActive(true);
        companyCa.setValid(true);


        return data;
    }





}
