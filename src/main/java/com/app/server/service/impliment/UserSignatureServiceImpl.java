package com.app.server.service.impliment;

import com.app.server.dto.request.SignatureRequest;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.dto.signatureDto.SignatureRequestDto;
import com.app.server.dto.signatureDto.SignatureResponseDto;
import com.app.server.exception.AppConflicException;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.Signature;
import com.app.server.model.User;
import com.app.server.model.UserSignature;
import com.app.server.repository.UserSignatureRepository;
import com.app.server.service.SignatureService;
import com.app.server.service.UserService;
import com.app.server.service.UserSignatureService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.apache.kafka.common.utils.Utils.safe;

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

    @Override
    public UserSignature findUserSignatureByOtp(String otp) {
        UserSignature signature = userSignatureRepository.findByOtp(otp).orElseThrow(()->new AppNotFoundException("امضا کاربر پیدا نشد"));
        return signature;
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

        // بررسی معتبر بودن OTP
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

            // لیست هشدار برای فیلدهای خالی
            List<String> warnings = new ArrayList<>();

            String username = existSignature.getUser().getUsername();
            if (username == null) warnings.add("username is null");

            String country = existSignature.getCountry();
            if (country == null) warnings.add("country is null");

            String reason = existSignature.getReason();
            if (reason == null) warnings.add("reason is null");

            String location = existSignature.getLocation();
            if (location == null) warnings.add("location is null");

            String organization = existSignature.getOrganization();
            if (organization == null) warnings.add("organization is null");

            String department = existSignature.getDepartment();
            if (department == null) warnings.add("department is null");

            String state = existSignature.getState();
            if (state == null) warnings.add("state is null");

            String city = existSignature.getCity();
            if (city == null) warnings.add("city is null");

            String email = existSignature.getEmail();
            if (email == null) warnings.add("email is null");

            String title = existSignature.getTitle();
            if (title == null) warnings.add("title is null");

            // ساخت DTO بدون مقدار فیک ("خالی")
            SignatureRequestDto req = SignatureRequestDto.builder()
                    .username(username)
                    .country(country)
                    .reason(reason)
                    .location(location)
                    .organization(organization)
                    .department(department)
                    .state(state)
                    .city(city)
                    .email(email)
                    .title(title)
                    .userId("")
                    .signatureExpired(existSignature.getSignature().getPeriod())
                    .signaturePassword(existSignature.getSignaturePassword())
                    .build();

//            // ارسال درخواست به سرویس امضا
            SignatureResponseDto result = sendSignatureRequest(req);

            // بروزرسانی دیتا
            existSignature.setKeyId(result.getUserId());
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
    public SignatureResponseDto sendSignatureRequest(SignatureRequestDto req) {

        String url = "http://localhost:8585/api/v1/signature/generate";

        // ساخت آبجکت جدید و اعمال مقادیر
        SignatureRequestDto request = new SignatureRequestDto();
        request.setUsername(req.getUsername());
        request.setCountry(req.getCountry());
        request.setReason(req.getReason());
        request.setLocation(req.getLocation());
        request.setOrganization(req.getOrganization());
        request.setDepartment(req.getDepartment());
        request.setState(req.getState());
        request.setCity(req.getCity());
        request.setEmail(req.getEmail());
        request.setTitle(req.getTitle());
        request.setUserId(req.getUserId());
        request.setSignatureExpired(10);
        request.setSignaturePassword(req.getSignaturePassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // اینجا باید ***request*** ارسال شود نه req
        HttpEntity<SignatureRequestDto> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<SignatureResponseDto> response =
                    restTemplate.postForEntity(url, entity, SignatureResponseDto.class);

            return response.getBody();

        } catch (HttpClientErrorException e) {
            throw new RuntimeException("خطا از سرویس امضا: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("خطای داخلی سرویس امضا: " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new RuntimeException("اتصال به سرویس امضا برقرار نشد");
        } catch (Exception e) {
            throw new RuntimeException("خطای نامشخص: " + e.getMessage());
        }
    }

}
