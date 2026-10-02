package com.app.server.service.impliment.Company;

import com.app.server.dto.response.Sodor24ResponseDto;
import com.app.server.exception.AppConflicException;
import com.app.server.exception.AppInternalException;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.CompanySubscriptionPlan;
import com.app.server.repository.CompanySubscriptionPlanRepository;
import com.app.server.service.CompanySubscriptionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanySubscriptionPlanServiceImpl
        implements CompanySubscriptionPlanService {

    private final CompanySubscriptionPlanRepository repository;

    private static final String CACHE_NAME = "CompanySubscriptionPlan";


    @Override
    @CacheEvict(
            value = CACHE_NAME,
            allEntries = true
    )
    public CompanySubscriptionPlan createCompanySubscriptionPlan(
            CompanySubscriptionPlan plan,
            String userSub
    ) throws AppConflicException {

        if (repository.existsByTitle(plan.getTitle())) {
            throw new AppConflicException(
                    "پلنی با این عنوان قبلاً ثبت شده است"
            );
        }

        plan.setCreatedBy(userSub);
        plan.setActive(false);

        return repository.save(plan);
    }


    @Override
    @CachePut(
            value = CACHE_NAME,
            key = "#slug"
    )
    public CompanySubscriptionPlan updateCompanySubscriptionPlanBySlug(
            String slug,
            CompanySubscriptionPlan request
    ) {

        CompanySubscriptionPlan existing =
                findCompanySubscriptionPlanBySlug(slug);

        existing.setTitle(request.getTitle());
        existing.setDescription(request.getDescription());
        existing.setCompletedDescription(request.getCompletedDescription());
        existing.setTags(request.getTags());
        existing.setValidDays(request.getValidDays());
        existing.setUsersCount(request.getUsersCount());
        existing.setPrice(request.getPrice());
        existing.setActive(request.getActive());

        return repository.save(existing);
    }


    @Override
    @Transactional(readOnly = true)
    public CompanySubscriptionPlan findCompanySubscriptionPlanById(Long id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new AppNotFoundException(
                                "پلن اشتراک پیدا نشد"
                        )
                );
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = CACHE_NAME,
            key = "#slug"
    )
    public CompanySubscriptionPlan findCompanySubscriptionPlanBySlug(
            String slug
    ) {

        return repository.findCompanySubscriptionPlanBySlug(slug)
                .orElseThrow(() ->
                        new AppNotFoundException(
                                "پلن اشتراک پیدا نشد"
                        )
                );
    }


    @Override
    @Transactional(readOnly = true)
    public List<CompanySubscriptionPlan> findAll() {

        return repository.findAll();
    }


    @Override
    @Transactional(readOnly = true)
    public List<CompanySubscriptionPlan> findActivePlans() {

        return repository.findAllByActiveTrue();
    }


    @Override
    @CacheEvict(
            value = CACHE_NAME,
            key = "#slug"
    )
    public Sodor24ResponseDto delete(String slug) {

        CompanySubscriptionPlan exist =
                findCompanySubscriptionPlanBySlug(slug);

        repository.delete(exist);

        return Sodor24ResponseDto.builder()
                .data("")
                .message("اشتراک مورد نظر حذف شد")
                .build();
    }


    @Override
    @CachePut(
            value = CACHE_NAME,
            key = "#slug"
    )
    public CompanySubscriptionPlan setActive(
            String slug,
            boolean value
    ) {

        CompanySubscriptionPlan find =
                findCompanySubscriptionPlanBySlug(slug);

        find.setActive(value);

        return repository.save(find);
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = CACHE_NAME,
            key = "'active:' + #slug"
    )
    public boolean checkActivePlan(
            String slug
    ) throws AppInternalException {

        CompanySubscriptionPlan subscriptionPlan =
                findCompanySubscriptionPlanBySlug(slug);

        if (Boolean.TRUE.equals(subscriptionPlan.getActive())) {
            return true;
        }

        throw new AppInternalException(
                "اشتراک در حالت تعلیق می‌باشد",
                "از اشتراک های دیگری استفاده نمایید"
        );
    }
}