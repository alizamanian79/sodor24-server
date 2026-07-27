package com.app.server.service.impliment;

import com.app.server.dto.request.SignaturePlanRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.SignaturePlan;
import com.app.server.repository.SignaturePlanRepository;
import com.app.server.service.SignaturePlanService;
import com.github.mfathi91.time.PersianDate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SignaturePlanServiceImpl implements SignaturePlanService {

    private final SignaturePlanRepository signaturePlanRepository;
    private final CacheManager cacheManager;

    public static final String SIGNATURE_PLAN_BY_ID_CACHE = "signaturePlanById";

    // ---------------------------------------------------------------
    // Cache-safe write helpers
    // ---------------------------------------------------------------

    /**
     * همه‌ی ذخیره‌سازی‌ها از این متد رد می‌شن تا کش همیشه با دیتابیس یکی باشه
     * (همون الگویی که توی SignatureServiceImpl استفاده کردیم).
     */
    private SignaturePlan persist(SignaturePlan plan) {
        SignaturePlan saved = signaturePlanRepository.save(plan);
        Cache cache = cacheManager.getCache(SIGNATURE_PLAN_BY_ID_CACHE);
        if (cache != null) {
            cache.put(saved.getId(), saved);
        }
        return saved;
    }

    private void evictAfterDelete(Long id) {
        Cache cache = cacheManager.getCache(SIGNATURE_PLAN_BY_ID_CACHE);
        if (cache != null) {
            cache.evict(id);
        }
    }

    // ---------------------------------------------------------------
    // Reads
    // ---------------------------------------------------------------

    @Override
    public List<SignaturePlan> getAllSignaturePlans() {
        return signaturePlanRepository.findAll();
    }

    @Cacheable(value = SIGNATURE_PLAN_BY_ID_CACHE, key = "#id")
    @Override
    public SignaturePlan findSignaturePlanById(Long id) {
        return signaturePlanRepository.findSignatureById(id)
                .orElseThrow(() -> new AppNotFoundException("امضای شما پیدا نشد"));
    }

    @Override
    public Page<SignaturePlan> getPageableSignaturesPlan(
            Integer page,
            Integer size,
            String search,
            String sortBy,
            String sortDir
    ) {
        Pageable pageable = Pageable.unpaged();
        if (page != null && size != null) {
            String field = (sortBy == null || sortBy.isBlank()) ? "id" : sortBy;
            Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            pageable = PageRequest.of(page, size, Sort.by(direction, field));
        }

        Specification<SignaturePlan> spec = (root, query, cb) -> cb.isTrue(root.get("isActive"));

        if (search != null && !search.isBlank()) {
            String keyword = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), keyword),
                    cb.like(cb.lower(root.get("description")), keyword)
            ));
        }

        return signaturePlanRepository.findAll(spec, pageable);
    }

    // ---------------------------------------------------------------
    // Writes
    // ---------------------------------------------------------------

    @Transactional
    @Override
    public SignaturePlan generateSignaturePlan(SignaturePlanRequestDto req) {
        req.setActive(false);

        SignaturePlan plan = SignaturePlan.builder()
                .creatorId(req.getCreatorId())
                .updatedUserId(0L)
                .title(req.getTitle())
                .description(req.getDescription())
                .price(req.getPrice())
                .usageCount(req.getUsageCount())
                .period(req.getPeriod())
                .features(req.getFeatures())
                .isActive(req.isActive())
                .tags(req.getTags())
                .build();

        return persist(plan);
    }

    @Transactional
    @Override
    public Object deleteSignaturePlan(Long id) {
        SignaturePlan plan = findSignaturePlanById(id);
        signaturePlanRepository.delete(plan);
        evictAfterDelete(id);

        return CustomResponseDto.builder()
                .status(HttpStatus.OK.value())
                .timestamp(PersianDate.now())
                .message("امضا مورد نظر حذف شد")
                .details("")
                .build();
    }

    @Transactional
    @Override
    public SignaturePlan updateSignaturePlanById(SignaturePlanRequestDto req, Long id) {
        SignaturePlan plan = findSignaturePlanById(id);

        plan.setUpdatedUserId(req.getUpdatedUserId());
        plan.setTitle(req.getTitle());
        plan.setDescription(req.getDescription());
        plan.setPrice(req.getPrice());
        plan.setUsageCount(req.getUsageCount());
        plan.setPeriod(req.getPeriod());
        plan.setActive(req.isActive());
        // این دو خط توی نسخه‌ی قبلی جا افتاده بودن: features و tags توی create
        // ست می‌شدن ولی توی update اصلاً آپدیت نمی‌شدن.
        plan.setFeatures(req.getFeatures());
        plan.setTags(req.getTags());

        return persist(plan);
    }

    @Transactional
    @Override
    public Object activeSignaturePlan(Long signatureId, boolean active) {
        SignaturePlan plan = findSignaturePlanById(signatureId);
        plan.setActive(active);
        SignaturePlan saved = persist(plan);

        String status = saved.isActive() ? "معتبر" : "نامعتبر";

        return CustomResponseDto.builder()
                .status(HttpStatus.OK.value())
                .timestamp(PersianDate.now())
                .message("امضا به حالت " + status + " در آمد")
                .details("")
                .build();
    }
}