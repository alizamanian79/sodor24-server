package com.app.server.service.impliment;

import com.app.server.dto.request.SignatureRequest;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.exception.AppConflicException;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.Signature;
import com.app.server.model.User;
import com.app.server.model.UserSignature;
import com.app.server.repository.UserSignatureRepository;
import com.app.server.service.SignatureService;
import com.app.server.service.UserService;
import com.app.server.service.UserSignatureService;
import com.app.server.util.rabbitMQ.dto.request.RMQSignatureRequestDto;
import com.app.server.util.rabbitMQ.dto.response.RMQResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mfathi91.time.PersianDate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.app.server.util.rabbitMQ.SignatureRMQProducer;
import static org.apache.kafka.common.utils.Utils.safe;

@Service
@RequiredArgsConstructor
public class UserSignatureServiceImpl implements UserSignatureService {

    private final SignatureService signatureService;
    private final UserService userService;
    private final UserSignatureRepository userSignatureRepository;
    private final SignatureRMQProducer signatureRMQProducer;

    @Autowired
    private RestTemplate restTemplate ;


    @Override
    public List<UserSignature> findAll() {
        List<UserSignature> list = userSignatureRepository.findAll();
        Collections.reverse(list);
        return list;
    }

    @Override
    public UserSignature findById(Long usersignatureId) {
        return userSignatureRepository.findById(usersignatureId).orElseThrow(()-> new AppNotFoundException("امضا پیدا نشد"));
    }

    @Override
    public UserSignature findUserSignatureByOtp(String otp) {
        UserSignature signature = userSignatureRepository.findByOtp(otp).orElseThrow(()->new AppNotFoundException("کد وارد شده نامعتبر میباشد"));
        return signature;
    }




    @Transactional
    @Override
    public UserSignature generateUserSignature(SignatureRequest req) {
        // find user and signature plan
        User existUser = userService.findUserById(req.getUserId());
        Signature existSignature =signatureService.findSignatureById(req.getSignatureId());

        if (!existSignature.isActive()){
            throw new AppConflicException("اعتبار این پلن از امضا تایید نشده",
                    "به محض تعویض وضعیت این پلن به شما اطلاع خواهیم داد");
        }

        // Generate Signature

        UserSignature userSignature = UserSignature.builder()
                .user(existUser)
                .signature(existSignature)
                .valid(false)
                .usageCount(existSignature.getUsageCount())
                .keyId(null)

                //username
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
                .signatureExpired(PersianDate.now().plusDays(existSignature.getPeriod()).toString())
                .expiredAt(LocalDateTime.now().plusDays(existSignature.getPeriod()))
                .build();
        UserSignature saved = userSignatureRepository.save(userSignature);
        return saved;
    }











    // Verify Transaction from signatur
    @Transactional
    @Override
    public CustomResponseDto verifySignature(String otp) {

        CustomResponseDto res = new CustomResponseDto();
        Optional<UserSignature> find = userSignatureRepository.findByOtp(otp);

        if (find.isEmpty()) {
            res.setStatus(HttpStatus.NOT_FOUND.value());
            res.setMessage("OTP نامعتبر است");
            res.setTimestamp(PersianDate.now());
            return res;
        }
        UserSignature existSignature = find.get();



        try {

//            existSignature.setKeyId("");
            existSignature.setValid(true);
            existSignature.setOtp(null);
            existSignature.setUsageCount(existSignature.getUsageCount() - 1);


            userSignatureRepository.save(existSignature);

            // پاسخ نهایی
            res.setStatus(HttpStatus.OK.value());
            res.setMessage("امضا با موفقیت تایید شد");
            res.setTimestamp(PersianDate.now());
            res.setDetails(existSignature.getKeyId());

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
    public void sendRequestToSignatureService(UserSignature req) {

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
                .userId("") // اگر userId دارید، مقدار دهید
                .signatureExpired(req.getSignature().getPeriod())
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
                req.setKeyId(p12 != null ? p12.toString() : null);
                userSignatureRepository.save(req);
            }
        }


    }



}
