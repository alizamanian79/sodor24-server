package com.app.server.service.impliment;

import com.app.server.dto.request.UserCaRequestDto;
import com.app.server.exception.AppBadRequestException;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.User;
import com.app.server.model.UserCa;
import com.app.server.model.companies.CompanyCa;
import com.app.server.repository.UserCaRepository;
import com.app.server.service.CompanyCaService;
import com.app.server.service.UserCaService;
import com.app.server.service.UserService;
import com.app.server.util.signature_service_producer.CertificateProducer;
import com.app.server.util.signature_service_producer.dto.request.ca.CreatePersonCertificateRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCaServiceImpl implements UserCaService {

    private final UserCaRepository userCaRepository;
    private final PasswordEncoder passwordEncoder;

    private final CertificateProducer certificateProducer;
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

        if (companyCa.getSignaturesCount()< req.getSignatureCount()){
            throw new AppBadRequestException("تعداد امضای اختصاص داده شده شما بیش از حد مجاز شرکت میباشد");
        }

        UserCa createUserCa = UserCa.builder()
                .companyCa(companyCa)
                .user(user)
                .amount(BigDecimal.ZERO)
                .isActive(false)
                .isValid(false)
                .caId("")
                .validityDays(req.getValidityDays())
                .signatureCount(req.getSignatureCount())
                .totalSignatureCount(req.getSignatureCount())
                .status("pending")
                .privateKeyPassword(passwordEncoder.encode(req.getPrivateKeyPassword()))
                .build();
      return   userCaRepository.save(createUserCa);

    }








    public UserCa findUserCaBySlug(String slug){
       return userCaRepository.findUserCaBySlug(slug)
                .orElseThrow(()->new AppNotFoundException("شناسه امضای کاربر یافت نشد."));
    }


    @Transactional
    public UserCa callBackUserCa(String slug){


        UserCa userca = findUserCaBySlug(slug);
        userca.setActive(true);
        userca.setValid(true);
        userca.setCaId("");
        userca.setStatus("paid");
        userca.setExpiredAt(LocalDateTime.now().plusDays(userca.getValidityDays()));


        CompanyCa companyCa = userca.getCompanyCa();
        companyCa.setSignaturesCount(companyCa.getSignaturesCount() - userca.getSignatureCount());

        return userca;
    }



    public Map<String,Object> sendRequestToService(UserCa ca){

        // Company ca id in signature service
        String companyId = ca.getCompanyCa().getCaId();
        String companyPassword = ca.getCompanyCa().getPrivateKeyPassword();

        // create a method for finding user


        CreatePersonCertificateRequestDto request = CreatePersonCertificateRequestDto.builder()
                .companyId(companyId)
                .userId(ca.getUser().getSub())
                .username("username")
                .department("")
                .country("")
                .state("")
                .location("")
                .email("")
                .validityDays(ca.getValidityDays())
                .privateKeyPassword(ca.getPrivateKeyPassword())
                .caPrivateKeyPassword(companyPassword)
                .build();
     return    certificateProducer.createPersonCertificate(request);
    }



    @Override
    public UserCa updateUserCaBySlug(String slug) {
        return null;
    }
}
