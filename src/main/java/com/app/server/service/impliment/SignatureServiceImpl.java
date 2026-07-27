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

import com.app.server.util.signature_service_producer.producer.SignatureProducer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mfathi91.time.PersianDate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignatureServiceImpl implements SignatureService {

    private final SignaturePlanService signaturePlanService;
    private final UserService userService;
    private final SignatureRepository signatureRepository;
    private final SignatureProducer signatureProducer;
    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper;

    public static final String SIGNATURE_BY_ID_CACHE = "signatureById";
    public static final String SIGNATURE_BY_OTP_CACHE = "signatureByOtp";

    // ---------------------------------------------------------------
    // Reads
    // ---------------------------------------------------------------

    @Override
    public List<Signature> findAll() {
        return signatureRepository.findAll();
    }

    @Cacheable(value = SIGNATURE_BY_ID_CACHE, key = "#id")
    @Override
    public Signature findById(Long id) {
        return signatureRepository.findById(id)
                .orElseThrow(() -> new AppNotFoundException("امضا پیدا نشد"));
    }

    @Cacheable(value = SIGNATURE_BY_OTP_CACHE, key = "#otp")
    @Override
    public Signature findSignatureByOtp(String otp) {
        return signatureRepository.findByOtp(otp)
                .orElseThrow(() -> new AppNotFoundException("کد وارد شده نامعتبر میباشد"));
    }

    // ---------------------------------------------------------------
    // Cache-safe write helpers
    // ---------------------------------------------------------------

    /**
     * همه‌ی ذخیره‌سازی‌ها از این متد عبور می‌کنن تا کش همیشه consistent باشه.
     * چون otp یک فیلد mutable و امنیتی هست (هر بار عوض میشه یا null میشه)،
     * به جای key کردن دستی و ناقص، کل کش OTP رو با هر save پاک می‌کنیم تا
     * هیچ‌وقت داده‌ی stale برنگرده.
     */
    private Signature persist(Signature signature) {
        Signature saved = signatureRepository.save(signature);

        Cache idCache = cacheManager.getCache(SIGNATURE_BY_ID_CACHE);
        if (idCache != null) {
            idCache.put(saved.getId(), saved);
        }
        Cache otpCache = cacheManager.getCache(SIGNATURE_BY_OTP_CACHE);
        if (otpCache != null) {
            otpCache.clear();
        }
        return saved;
    }

    private void evictAfterDelete(Long id) {
        Cache idCache = cacheManager.getCache(SIGNATURE_BY_ID_CACHE);
        if (idCache != null) {
            idCache.evict(id);
        }
        Cache otpCache = cacheManager.getCache(SIGNATURE_BY_OTP_CACHE);
        if (otpCache != null) {
            otpCache.clear();
        }
    }

    // ---------------------------------------------------------------
    // Writes
    // ---------------------------------------------------------------

    @Transactional
    @Override
    public Signature generateSignature(SignatureRequestDto req) {
        User user = userService.findUserById(req.getUserId());
        SignaturePlan plan = signaturePlanService.findSignaturePlanById(req.getSignaturePlanId());

        if (!plan.isActive()) {
            throw new AppConflicException(
                    "اعتبار این پلن از امضا تایید نشده",
                    "به محض تعویض وضعیت این پلن به شما اطلاع خواهیم داد");
        }

        Signature signature = Signature.builder()
                .user(user)
                .signaturePlan(plan)
                .valid(false)
                .usageCount(plan.getUsageCount())
                .totalPrice(plan.getPrice())
                .status("در انتظار تایید کد")
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
                .signatureExpired(LocalDateTime.now().plusDays(plan.getPeriod()))
                .build();

        return persist(signature);
    }

    /**
     * تایید OTP.
     * نکته‌ی مهم: چون signature از طریق findByOtp(otp) پیدا شده بود، شرط
     * existSignature.getOtp().equals(otp) همیشه true بود و شاخه‌ی else
     * (کد نامعتبر) در عمل هرگز اجرا نمی‌شد — این منطق مرده حذف شد.
     * اگه نیاز به expiry واقعی برای OTP هست، باید یک فیلد otpExpiredAt
     * به Entity اضافه بشه و اینجا چک بشه.
     */
    @Transactional
    @Override
    public CustomResponseDto verifySignature(String otp) {
        Optional<Signature> found = signatureRepository.findByOtp(otp);

        if (found.isEmpty()) {
            return CustomResponseDto.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message("OTP نامعتبر است")
                    .timestamp(PersianDate.now())
                    .build();
        }

        Signature signature = found.get();

        try {
            signature.setValid(false);
            signature.setStatus("در انتظار پرداخت");
            signature.setOtp(null);
            persist(signature);

            return CustomResponseDto.builder()
                    .status(HttpStatus.OK.value())
                    .message("احراز هویت با موفقیت انجام شد")
                    .timestamp(PersianDate.now())
                    .build();

        } catch (Exception e) {
            log.error("خطا در verifySignature برای signature id={}", signature.getId(), e);
            signature.setOtp(String.valueOf(1000 + new Random().nextInt(9000)));
            signature.setStatus("عدم تایید احراز هویت");
            signature.setValid(false);
            persist(signature);

            return CustomResponseDto.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("ERROR")
                    .timestamp(PersianDate.now())
                    .build();
        }
    }

    @Override
    public boolean sendRequestToSignatureService(Signature req) {
        try {
            com.app.server.util.signature_service_producer.dto.request.SignatureRequestDto signatureServiceReq =
                    com.app.server.util.signature_service_producer.dto.request.SignatureRequestDto.builder()
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

            Object res = signatureProducer.generateSignature(signatureServiceReq);
            log.info("Response from signature service: {}", res);

            if (res == null) {
                return false;
            }

            Map<String, Object> converted = objectMapper.convertValue(res, new TypeReference<>() {});
            Object dataObj = converted.get("data");

            if (dataObj instanceof Map<?, ?> dataMap) {
                Object id = dataMap.get("userId");
                if (id != null && !id.toString().isBlank()) {
                    req.setPrivateKeyId(id + ".p12");
                    return true;
                }
            }
            return false;

        } catch (Exception e) {
            log.error("Error in sendRequestToSignatureService", e);
            return false;
        }
    }

    @Transactional
    @Override
    public CustomResponseDto deleteSignature(Long id) {
        Signature existing = findById(id);
        signatureRepository.delete(existing);
        evictAfterDelete(id);

        return CustomResponseDto.builder()
                .message("امضا حذف شد")
                .status(HttpStatus.OK.value())
                .timestamp(PersianDate.now())
                .build();
    }

    @Transactional
    @Override
    public Signature updateSignature(Long id, SignatureRequestDto req) {
        Signature existing = findById(id);

        if (existing.isValid()) {
            // به جای برگردوندن null بی‌صدا (که caller ممکنه چک نکنه و NPE بگیره)،
            // یک خطای معنادار پرتاب می‌کنیم.
            throw new AppConflicException(
                    "امضای معتبر قابل ویرایش نیست",
                    "برای ویرایش، ابتدا امضا را غیرفعال کنید");
        }

        SignaturePlan plan = signaturePlanService.findSignaturePlanById(req.getSignaturePlanId());
        User user = userService.findUserById(req.getUserId());

        existing.setSignaturePlan(plan);
        existing.setUser(user);
        existing.setUsageCount(plan.getUsageCount());
        existing.setTotalUsageCount(plan.getUsageCount());
        existing.setValid(false);
        existing.setPrivateKeyId(null);
        existing.setOtp(String.valueOf(1000 + new Random().nextInt(9000)));
        existing.setCountry(req.getCountry());
        existing.setReason(req.getReason());
        existing.setLocation(req.getLocation());
        existing.setOrganization(req.getOrganization());
        existing.setDepartment(req.getDepartment());
        existing.setState(req.getState());
        existing.setCity(req.getCity());
        existing.setEmail(req.getEmail());
        existing.setTitle(req.getTitle());
        existing.setSignatureExpired(LocalDateTime.now().plusDays(plan.getPeriod()));

        return persist(existing);
    }

    @Transactional
    @Override
    public Signature changeSignatureValid(Long id, boolean valid) {
        Signature signature = findById(id);
        signature.setValid(valid);
        return persist(signature);
    }

    @Transactional
    @Override
    public boolean useSignature(Signature req) {
        Signature signature = findById(req.getId());

        if (!signature.isValid()) {
            throw new AppBadRequestException("امضای شما معتبر نمی‌باشد.");
        }

        if (LocalDateTime.now().isAfter(signature.getSignatureExpired())) {
            signature.setValid(false);
            persist(signature);
            throw new AppBadRequestException("تاریخ امضای شما به پایان رسیده است.");
        }

        if (signature.getUsageCount() <= 0) {
            signature.setValid(false);
            persist(signature);
            throw new AppBadRequestException("تعداد استفاده این پلن امضا به پایان رسیده است.");
        }

        signature.setUsageCount(signature.getUsageCount() - 1);
        if (signature.getUsageCount() == 0) {
            signature.setValid(false);
        }
        persist(signature);

        return true;
    }

    @Transactional
    @Override
    public Signature updateSignatureIntenral(Signature req) {
        return persist(req);
    }

    @Transactional
    @Override
    public CustomResponseDto generateSignatureKeys(Long signatureId) {
        Signature signature = findById(signatureId);

        boolean success = sendRequestToSignatureService(signature);
        if (!success) {
            throw new AppBadRequestException("خطا در ارتباط با سرویس امضا");
        }

        signature.setValid(true);
        signature.setStatus("معتبر");
        persist(signature);

        return CustomResponseDto.builder()
                .message("کلید شما ساخته شد")
                .details(signature.getPrivateKeyId())
                .build();
    }
}