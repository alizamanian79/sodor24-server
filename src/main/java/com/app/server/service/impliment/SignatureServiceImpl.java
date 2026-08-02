package com.app.server.service.impliment;

import com.app.server.dto.request.SignatureRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.dto.response.Sodor24ResponseDto;
import com.app.server.exception.AppBadRequestException;
import com.app.server.exception.AppConflicException;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.OtpType;
import com.app.server.model.SignaturePlan;
import com.app.server.model.User;
import com.app.server.model.Signature;
import com.app.server.repository.SignatureRepository;
import com.app.server.service.OtpService;
import com.app.server.service.SignaturePlanService;
import com.app.server.service.UserService;
import com.app.server.service.SignatureService;

import com.app.server.service.impliment.OtpFactory.OtpFactory;
import com.app.server.util.signature_service_producer.producer.SignatureProducer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mfathi91.time.PersianDate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
public class SignatureServiceImpl implements SignatureService {

    private final SignaturePlanService signaturePlanService;
    private final UserService userService;
    private final SignatureRepository signatureRepository;
    private final SignatureProducer signatureProducer;
    private final OtpFactory otpFactory;

    private final Executor taskExecutor;


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



    private CompletableFuture<Signature> generateSignatureAsync(
            User existUser,
            SignaturePlan signaturePlan,
            SignatureRequestDto req) {

        return CompletableFuture.supplyAsync(() -> {

            Signature signature = Signature.builder()
                    .user(existUser)
                    .signaturePlan(signaturePlan)
                    .valid(false)
                    .usageCount(signaturePlan.getUsageCount())
                    .totalPrice(signaturePlan.getPrice())
                    .status("در انتظار تایید کد")
                    .country(req.getCountry())
                    .reason(req.getReason())
                    .location(req.getLocation())
                    .organization(req.getOrganization())
                    .department(req.getDepartment())
                    .state(req.getState())
                    .city(req.getCity())
                    .email(req.getEmail())
                    .title(req.getTitle())
                    .signaturePassword(req.getSignaturePassword())
                    .signatureExpired(LocalDateTime.now().plusDays(signaturePlan.getPeriod()))
                    .build();

            Signature saved = signatureRepository.save(signature);

//            OtpService otpService = otpFactory.getService(OtpType.SIGNATURE);
//            otpService.generateOtp(saved.getId().toString());

            return saved;
        },taskExecutor);
    }


    @Transactional
    @Override
    public Signature generateSignature(SignatureRequestDto req) {

        User existUser = userService.findUserById(req.getUserId());
        SignaturePlan signaturePlan =
                signaturePlanService.findSignaturePlanById(req.getSignaturePlanId());

        CompletableFuture<Signature> future =
                generateSignatureAsync(existUser, signaturePlan, req);

        return future.join();
    }




    // Verify Transaction from signature
//    @Transactional
//    @Override
//    public Object verifySignature(String otp) {
//
//        Optional<Signature> find = signatureRepository.findByOtp(otp);
//        Signature existSignature = find.get();
//
//        try {
//            if (existSignature.getOtp().equals(otp)) {
//                existSignature.setValid(true);
//                existSignature.setStatus("در انتظار پرداخت");
//                existSignature.setOtp(null);
//                signatureRepository.save(existSignature);
//                return existSignature;
//            }
//
//
//            else if (existSignature.equals(null)){
//                existSignature.setValid(false);
//                existSignature.setOtp(String.valueOf(1000 + new Random().nextInt(9000)));
//                existSignature.setStatus("عدم احراز هویت");
//                res.setStatus(HttpStatus.BAD_GATEWAY.value());
//                res.setMessage("SERVER");
//            }
//
//
//
//
//            res.setMessage("احراز هویت با موفقیت انجام شد");
//            signatureRepository.save(existSignature);
//            res.setTimestamp(PersianDate.now());
//
//            return Sodor24ResponseDto;
//
//        } catch (Exception e) {
//            throw new AppBadRequestException("کد تایید شما اشتباه میباشد");
//        }
//    }










    @Override
    public boolean sendRequestToSignatureService(Signature req) {
        try {
            com.app.server.util.signature_service_producer.dto.request.SignatureRequestDto signatureserviceReq = com.app.server.util.signature_service_producer.dto.request.SignatureRequestDto.builder()
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

            Object res = signatureProducer.generateSignature(signatureserviceReq);
            System.out.println("Response from signature service: " + res);

            if (res != null) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> convert = mapper.convertValue(res, new TypeReference<Map<String, Object>>() {});

                Object dataObj = convert.get("data");
                if (dataObj instanceof Map<?, ?> dataMap) {
                    Object p12 = dataMap.get("p12");
                    Object id = dataMap.get("userId");

                    if (id != null && !id.toString().isBlank()) {
                        req.setPrivateKeyId(id.toString()+".p12");

                        return true; // موفقیت
                    }
                }
            }
            return false; // عدم موفقیت

        } catch (Exception e) {
            System.err.println("Error in sendRequestToSignatureService: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }




    @Transactional
    @Override
    public CustomResponseDto deleteSignature(Long id) {

        Signature existSignature = signatureRepository.findSignatureById(id).orElseThrow(()->new AppNotFoundException("امضا پیدا نشد"));
        signatureRepository.deleteSignatureById(id);

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




    @Transactional
    @Override
    public Signature updateSignatureIntenral(Signature req) {
        return signatureRepository.save(req);
    }


    @Transactional
    @Override
    public CustomResponseDto generateSignatureKeys(Long signatureId){
        try{
            Signature req = findById(signatureId);
            boolean signatureSuccess = sendRequestToSignatureService(req);

            if (!signatureSuccess){
                throw new AppBadRequestException("خطا");
            }

            req.setValid(true);
            req.setStatus("معتبر");
            signatureRepository.save(req);

            CustomResponseDto res =  CustomResponseDto.builder()
                    .message("کلید شما ساخته شد")
                    .details(req.getPrivateKeyId())
                    .build();
            return res;

        } catch (Exception e) {
            throw new AppBadRequestException(e.getMessage());
        }


    }
}
