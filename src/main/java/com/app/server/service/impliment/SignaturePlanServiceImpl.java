package com.app.server.service.impliment;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.SignaturePlan;
import com.app.server.repository.SignaturePlanRepository;
import com.app.server.service.SignaturePlanService;
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

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SignaturePlanServiceImpl implements SignaturePlanService {

    private final SignaturePlanRepository signaturePlanRepository;



    // Get all signature plans
    @Override
    public List<SignaturePlan> getAllSignaturePlans() {
        List<SignaturePlan> all = signaturePlanRepository.findAll();
        Collections.reverse(all);
        return all;
    }

    // Find signature plan by Id
    @Override
    public SignaturePlan findSignaturePlanById(Long id) {
        return signaturePlanRepository.findSignatureById(id)
                .orElseThrow(() -> new AppNotFoundException("امضای شما پیدا نشد"));
    }

    // Generate signature plan
    @Override
    public SignaturePlan generateSignaturePlan(SignaturePlan signature) {
        signature.setActive(false);
        return signaturePlanRepository.save(signature);
    }

    // Delete signature plan
    @Override
    public Object deleteSignaturePlan(Long signatureId) {
        SignaturePlan signature = findSignaturePlanById(signatureId);
        signaturePlanRepository.delete(signature);
        CustomResponseDto responseDto = CustomResponseDto.builder()
                .status(HttpStatus.OK.value())
                .timestamp(PersianDate.now())
                .message("امضا مورد نظر حذف شد")
                .details("")
                .build();
        return responseDto;
    }


    // Update signature plan
    @Transactional
    @Override
    public SignaturePlan updateSignaturePlan(SignaturePlan signature) {
        SignaturePlan findSignature = findSignaturePlanById(signature.getId());
        findSignature.setTitle(signature.getTitle());
        findSignature.setDescription(signature.getDescription());
        findSignature.setPrice(signature.getPrice());
        findSignature.setUsageCount(signature.getUsageCount());
        findSignature.setPeriod(signature.getPeriod());
        findSignature.setActive(signature.isActive());
        signaturePlanRepository.save(findSignature);
        return findSignature;
    }


    // Signature plan Active status
    @Transactional
    @Override
    public Object activeSignaturePlan(Long signatureId , boolean active) {
        SignaturePlan signature = findSignaturePlanById(signatureId);
        signature.setActive(active);
        SignaturePlan saved = signaturePlanRepository.save(signature);

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
    public Page<SignaturePlan> getPageableSignaturesPlan(
            Integer page,
            Integer size,
            String search,
            String sortBy,
            String sortDir
    ) {

        Pageable pageable = Pageable.unpaged();
        if (page != null && size != null) {
            Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC, sortBy);

            pageable = PageRequest.of(page, size, sort);
        }

        Specification<SignaturePlan> spec = Specification.where(null);

        spec = spec.and((root, query, cb) -> cb.isTrue(root.get("active")));

        if (search != null && !search.isBlank()) {
            String keyword = "%" + search.toLowerCase() + "%";

            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), keyword),
                    cb.like(cb.lower(root.get("description")), keyword)
            ));
        }

        return signaturePlanRepository.findAll(spec, pageable);
    }


}
