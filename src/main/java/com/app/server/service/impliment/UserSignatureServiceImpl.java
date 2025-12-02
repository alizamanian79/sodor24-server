package com.app.server.service.impliment;

import com.app.server.dto.request.SignatureRequest;
import com.app.server.dto.signatureMicroServiceDto.SignatureRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.dto.signatureMicroServiceDto.SignatureResponseDto;
import com.app.server.exception.AppConflicException;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.Signature;
import com.app.server.model.User;
import com.app.server.model.UserSignature;
import com.app.server.repository.UserSignatureRepository;
import com.app.server.service.SignatureService;
import com.app.server.service.UserService;
import com.app.server.service.UserSignatureService;
import com.github.mfathi91.time.PersianDate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSignatureServiceImpl implements UserSignatureService {

    private final SignatureService signatureService;
    private final UserService userService;
    private final UserSignatureRepository userSignatureRepository;

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


    @Transactional
    @Override
    public UserSignature generateUserSignature(SignatureRequest req) {
        // find user and signature plan
        User existUser = userService.findUserById(req.getUserId());
        Signature existSignature =signatureService.findSignatureById(req.getSignatureId());

        if (!existSignature.isActive()){
            throw new AppConflicException("اعتبار این پلن از امضا تایید نشده","به محض تعویض وضعیت این پلن به شما اطلاع خواهیم داد");
        }

        // Generate Signature
        UserSignature userSignature = UserSignature.builder()
                .user(existUser)
                .signature(existSignature)
                .valid(false)
                .usageCount(existSignature.getUsageCount())
                .keyId(null)


                .city(req.getCity())
                .country(req.getCountry())
                .state(req.getState())
                .title(req.getTitle())
                .department(req.getDepartment())
                .organization(req.getOrganization())
                .location(req.getLocation())
                .email(req.getEmail())
                .title(req.getTitle())
                .reason(req.getReason())

                .signaturePassword(req.getSignaturePassword())
                .signatureExpired(PersianDate.now().plusDays(existSignature.getPeriod()).toString())
                .expiredAt(LocalDateTime.now().plusDays(existSignature.getPeriod()))
                .build();
        UserSignature saved = userSignatureRepository.save(userSignature);
        return saved;
    }


    // Using Signature
    @Override
    @Transactional
    public boolean callBackSignatureProcess(Long id){
     UserSignature signature = findById(id);
     if (signature.isValid()){
         if (signature.getUsageCount() <=0){
             return false;
         }
         signature.setUsageCount(signature.getUsageCount() - 1);
         userSignatureRepository.save(signature);
         return true;
     }
     return false;
    }

    @Transactional
    @Override
    public Object verifySignature(String otp) {

        CustomResponseDto res = new CustomResponseDto();

        Optional<UserSignature> find = userSignatureRepository.findByOtp(otp);

        // بررسی موجودیت OTP
        if (find.isEmpty()) {
            res.setStatus(HttpStatus.NOT_FOUND.value());
            res.setMessage("OTP نامعتبر است");
            res.setTimestamp(PersianDate.now());
            return res;
        }

        UserSignature existSignature = find.get();

        // بررسی تعداد استفاده
        if (existSignature.getUsageCount() <= 0) {
            res.setStatus(HttpStatus.BAD_REQUEST.value());
            res.setMessage("تعداد استفاده امضا به پایان رسیده است");
            res.setTimestamp(PersianDate.now());
            return res;
        }

        try {
            // آماده‌سازی درخواست به سرویس امضا
            SignatureRequestDto req = SignatureRequestDto.builder()
                    .city(existSignature.getCity())
                    .country(existSignature.getCountry())
                    .state(existSignature.getState())
                    .department(existSignature.getDepartment())
                    .title(existSignature.getTitle())
                    .valid(true)
                    .email(existSignature.getEmail()) // اگر فیلد ایمیل دارید
                    .location(existSignature.getLocation())
                    .usageCount(existSignature.getUsageCount())
                    .reason(existSignature.getReason())
                    .username(existSignature.getUser().getUsername())
                    .signatureExpired(existSignature.getSignature().getPeriod())
                    .signaturePassword(existSignature.getSignaturePassword())
                    .organization(existSignature.getOrganization())
                    .build();

            String url = "http://localhost:8585/api/v1/signature/generate";
            ResponseEntity<SignatureResponseDto> response =
                    restTemplate.postForEntity(url, req, SignatureResponseDto.class);
            SignatureResponseDto result = response.getBody();


            // بروزرسانی دیتابیس
            existSignature.setKeyId(result.getUserId());
            existSignature.setValid(true);
            existSignature.setOtp(null);
            userSignatureRepository.save(existSignature);

            // پاسخ موفق
            res.setStatus(HttpStatus.OK.value());
            res.setMessage("امضای شما تایید شد");
            res.setTimestamp(PersianDate.now());
            return res;

        } catch (Exception e) {
            // مدیریت خطاهای سرویس امضا
            res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            res.setMessage("خطا در ارتباط با سرویس امضا: " + e.getMessage());
            res.setTimestamp(PersianDate.now());
            return res;
        }
    }

    public SignatureResponseDto sendRequestToSignatureService(SignatureRequestDto req) {
        String url = "http://localhost:8585/api/v1/signature/generate";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<SignatureRequestDto> entity = new HttpEntity<>(req, headers);

            ResponseEntity<SignatureResponseDto> response =
                    restTemplate.postForEntity(url, entity, SignatureResponseDto.class);

            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("خطا در ارتباط با سرویس امضا: " + e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            throw new RuntimeException("سرویس امضا در دسترس نیست: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("خطای غیرمنتظره: " + e.getMessage());
        }
    }


}
