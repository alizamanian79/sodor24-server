package com.app.server.service.impliment;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.Signature;
import com.app.server.repository.SignatureRepository;
import com.app.server.service.SignatureService;
import com.github.mfathi91.time.PersianDate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SignatureServiceImpl implements SignatureService {

    private final SignatureRepository signatureRepository;

    private final RestTemplate restTemplate;




    @Override
    public List<Signature> getSignatures() {
        List<Signature> all = signatureRepository.findAll();
        Collections.reverse(all);
        return all;
    }

    @Override
    public Signature findSignatureById(Long id) {
        return signatureRepository.findSignatureById(id)
                .orElseThrow(() -> new AppNotFoundException("امضای شما پیدا نشد"));
    }

    @Override
    public Signature generateSignature(Signature signature) {
        signature.setActive(false);
        return signatureRepository.save(signature);
    }

    @Override
    public Object deleteSignature(Long signatureId) {
        Signature signature = findSignatureById(signatureId);
        signatureRepository.delete(signature);
        CustomResponseDto responseDto = CustomResponseDto.builder()
                .status(HttpStatus.OK.value())
                .timestamp(PersianDate.now())
                .message("امضا مورد نظر حذف شد")
                .details("")
                .build();
        return responseDto;
    }

    @Transactional
    @Override
    public Signature updateSignature(Signature signature) {
        Signature findSignature = findSignatureById(signature.getId());
        findSignature.setTitle(signature.getTitle());
        findSignature.setDescription(signature.getDescription());
        findSignature.setPrice(signature.getPrice());
        findSignature.setUsageCount(signature.getUsageCount());
        findSignature.setPeriod(signature.getPeriod());
        findSignature.setActive(signature.isActive());
        signatureRepository.save(findSignature);
        return findSignature;
    }

    @Transactional
    @Override
    public Object activeSignature(Long signatureId , boolean active) {
        Signature signature = findSignatureById(signatureId);
        signature.setActive(active);
        Signature saved = signatureRepository.save(signature);

        String status = signature.isActive() == true ? " \s معتبر \s" :" \s نا معتبر \s";
        CustomResponseDto res = CustomResponseDto.builder()
                .status(HttpStatus.OK.value())
                .timestamp(PersianDate.now())
                .message("امضا به حالت" + status +"در امد")
                .details("")
                .build();
        return res;
    }


    @Override
    public Page<Signature> getPageableSignatures(
            Integer page,
            Integer size,
            String search,
            String sortBy,
            String sortDir
    ) {

        // اگر page و size خالی بود → بدون صفحه‌بندی همه را برگردان
        Pageable pageable = Pageable.unpaged();
        if (page != null && size != null) {
            Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC, sortBy);

            pageable = PageRequest.of(page, size, sort);
        }

        Specification<Signature> spec = Specification.where(null);

        // 🔒 فقط رکوردهای active = true
        spec = spec.and((root, query, cb) -> cb.isTrue(root.get("active")));

        // سرچ روی فیلدهای title و description
        if (search != null && !search.isBlank()) {
            String keyword = "%" + search.toLowerCase() + "%";

            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), keyword),
                    cb.like(cb.lower(root.get("description")), keyword)
            ));
        }

        return signatureRepository.findAll(spec, pageable);
    }


}
