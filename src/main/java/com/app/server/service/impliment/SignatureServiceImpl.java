package com.app.server.service.impliment;

import com.app.server.dto.request.SignatureRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.exception.AppBadRequestException;
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
import java.util.*;

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
    public Signature findSignatureByOtp(String otp) {
        Signature signature = signatureRepository.findByOtp(otp).orElseThrow(()->new AppNotFoundException("کد وارد شده نامعتبر میباشد"));
        return signature;
    }




    @Transactional
    @Override
    public Signature generateSignature(SignatureRequestDto req) {
        User existUser = userService.findUserById(req.getUserId());
        SignaturePlan signaturePlan = signaturePlanService.findSignaturePlanById(req.getSignaturePlanId());

        if (!signaturePlan.isActive()){
            throw new AppConflicException("اعتبار این پلن از امضا تایید نشده",
                    "به محض تعویض وضعیت این پلن به شما اطلاع خواهیم داد");
        }


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
                .signatureExpired(LocalDateTime.now().plusDays(signaturePlan.getPeriod()))
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
            existSignature.setUsageCount(existSignature.getUsageCount());


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


    @Transactional
    @Override
    public CustomResponseDto deleteSignature(Long id) {
        Signature existSignature = findById(id);
        signatureRepository.delete(existSignature);
        CustomResponseDto res = CustomResponseDto.builder()
                .message("امضا حذف شد")
                .status(HttpStatus.OK.value())
                .timestamp(PersianDate.now())
                .build();
        return res;
    }

    @Transactional
    @Override
    public Signature updateSignature(Long id , SignatureRequestDto req)  {



        Signature findSignature = findById(id);

        if (findSignature.isValid()) {
            return null;
        }

        SignaturePlan findPlan = signaturePlanService.findSignaturePlanById(req.getSignaturePlanId());
        User findUser = userService.findUserById(req.getUserId());

        findSignature.setSignaturePlan(findPlan);
        findSignature.setUser(findUser);

        findSignature.setUsageCount(findPlan.getUsageCount());
        findSignature.setTotalUsageCount(findPlan.getUsageCount());

        findSignature.setValid(false);
        findSignature.setPrivateKeyId(null);
        findSignature.setPrivateKeyIdLink(null);

        findSignature.setOtp(String.valueOf(1000 + new Random().nextInt(9000)));
        findSignature.setCountry(req.getCountry());
        findSignature.setReason(req.getReason());
        findSignature.setLocation(req.getLocation());
        findSignature.setOrganization(req.getOrganization());
        findSignature.setDepartment(req.getDepartment());
        findSignature.setState(req.getState());
        findSignature.setCity(req.getCity());
        findSignature.setEmail(req.getEmail());
        findSignature.setTitle(req.getTitle());
        findSignature.setSignatureExpired(LocalDateTime.now().plusDays(findPlan.getPeriod()));

       return signatureRepository.save(findSignature);


    }


    @Transactional
    @Override
    public Signature changeSignatureValid(Long id,boolean valid) {
        Signature signature = findById(id);
        signature.setValid(valid);
        Signature saved = signatureRepository.save(signature);
        return saved;
    }


    // Using Signature
    @Transactional
    @Override
    public boolean useSignature(Signature req) {

        Signature signature = findById(req.getId());

        if (!signature.isValid()) {
            throw new AppBadRequestException("امضای شما معتبر نمی‌باشد.");
        }

        if (LocalDateTime.now().isAfter(signature.getSignatureExpired())) {
            signature.setValid(false);
            signatureRepository.save(signature);
            throw new AppBadRequestException("تاریخ امضای شما به پایان رسیده است.");
        }


        if (signature.getUsageCount() <= 0) {
            signature.setValid(false);
            signatureRepository.save(signature);
            throw new AppBadRequestException("تعداد استفاده این پلن امضا به پایان رسیده است.");
        }


        signature.setUsageCount(signature.getUsageCount() - 1);

        if (signature.getUsageCount() == 0) {
            signature.setValid(false);
        }

        signatureRepository.save(signature);

        return true;
    }
}
