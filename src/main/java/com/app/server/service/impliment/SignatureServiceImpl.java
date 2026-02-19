package com.app.server.service.impliment;

import com.app.server.dto.request.SignatureRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.exception.AppConflicException;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.SignaturePlan;
import com.app.server.model.User;
import com.app.server.model.Signature;
import com.app.server.repository.SignatureRepository;
import com.app.server.service.SignaturePlanService;
import com.app.server.service.UserService;
import com.app.server.service.SignatureService;
import com.app.server.util.rabbitMQ.dto.request.RMQSignatureRequestDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mfathi91.time.PersianDate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.app.server.util.rabbitMQ.SignatureRMQProducer;

@Service
@RequiredArgsConstructor
public class SignatureServiceImpl implements SignatureService {

    private final SignaturePlanService signaturePlanService;
    private final UserService userService;
    private final SignatureRepository signatureRepository;
    private final SignatureRMQProducer signatureRMQProducer;



    @Override
    public List<Signature> findAll() {
        List<Signature> list = signatureRepository.findAll();
        Collections.reverse(list);
        return list;
    }

    @Override
    public Signature findById(Long id) {
        return signatureRepository.findById(id).orElseThrow(()-> new AppNotFoundException("امضا پیدا نشد"));
    }

    @Override
    public Signature findUserSignatureByOtp(String otp) {
        Signature signature = signatureRepository.findByOtp(otp).orElseThrow(()->new AppNotFoundException("کد وارد شده نامعتبر میباشد"));
        return signature;
    }




    @Transactional
    @Override
    public Signature generateUserSignature(SignatureRequestDto req) {
        // find user and signature plan
        User existUser = userService.findUserById(req.getUserId());
        SignaturePlan signaturePlan = signaturePlanService.findSignaturePlanById(req.getSignaturePlanId());

        if (!signaturePlan.isActive()){
            throw new AppConflicException("اعتبار این پلن از امضا تایید نشده",
                    "به محض تعویض وضعیت این پلن به شما اطلاع خواهیم داد");
        }

        // Generate Signature
        Signature signature = Signature.builder()
                .user(existUser)
                .signaturePlan(signaturePlan)
                .valid(false)
                .usageCount(signaturePlan.getUsageCount())

                .country(req.getCountry().toString())
                .reason(req.getReason().toString())
                .location(req.getLocation().toString())
                .organization(req.getOrganization().toString())
                .department(req.getDepartment().toString())
                .state(req.getState().toString())
                .city(req.getCity().toString())
                .email(req.getEmail().toString())
                .title(req.getTitle().toString())
                .signaturePassword(req.getSignaturePassword())
                .signatureExpired(PersianDate.now().plusDays(signaturePlan.getPeriod()).toString())
                .expiredAt(LocalDateTime.now().plusDays(signaturePlan.getPeriod()))
                .build();
        Signature saved = signatureRepository.save(signature);
        return saved;
    }











    // Verify Transaction from signatur
    @Transactional
    @Override
    public CustomResponseDto verifySignature(String otp) {

        CustomResponseDto res = new CustomResponseDto();
        Optional<Signature> find = signatureRepository.findByOtp(otp);

        if (find.isEmpty()) {
            res.setStatus(HttpStatus.NOT_FOUND.value());
            res.setMessage("OTP نامعتبر است");
            res.setTimestamp(PersianDate.now());
            return res;
        }
        Signature existSignature = find.get();



        try {

//            existSignature.setKeyId("");
            existSignature.setValid(true);
            existSignature.setOtp(null);
            existSignature.setUsageCount(existSignature.getUsageCount() - 1);


            signatureRepository.save(existSignature);

            // پاسخ نهایی
            res.setStatus(HttpStatus.OK.value());
            res.setMessage("امضا با موفقیت تایید شد");
            res.setTimestamp(PersianDate.now());


            return res;

        } catch (Exception e) {
            res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            res.setMessage("خطا در ارتباط با سرویس امضا: " + e.getMessage());
            res.setTimestamp(PersianDate.now());
            return res;
        }
    }



    @Transactional
    @Override
    public void sendRequestToSignatureService(Signature req) {

        RMQSignatureRequestDto signatureserviceReq = RMQSignatureRequestDto.builder()
                .username(req.getUser().getFullName())
                .country(req.getCountry())
                .reason(req.getReason())
                .location(req.getLocation())
                .organization(req.getOrganization())
                .department(req.getDepartment())
                .state(req.getState())
                .city(req.getCity())
                .email(req.getEmail())
                .title(req.getTitle())
                .userId("")
                .signatureExpired(req.getSignaturePlan().getPeriod())
                .signaturePassword(req.getSignaturePassword())
                .build();

        Object res = signatureRMQProducer.sendAndReceive(signatureserviceReq);

        System.out.println(res);

        ObjectMapper mapper = new ObjectMapper();

        if (res != null) {

            Map<String, Object> convert = mapper.convertValue(res, new TypeReference<Map<String, Object>>() {});

            Object dataObj = convert.get("data");
            if (dataObj instanceof Map<?, ?> dataMap) {

                Object p12 = dataMap.get("p12");
                System.out.println(p12);

                Object id = dataMap.get("userId");
                System.out.println(p12);

                req.setPrivateKeyIdLink(p12 != null ? p12.toString() : null);
                req.setPrivateKeyId(id.toString());
                signatureRepository.save(req);
            }
        }


    }



}
