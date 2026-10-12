package com.app.server.service.impliment.Company;

import com.app.server.dto.request.companies.CreateCompanySubscriptionPlanRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.exception.AppConflicException;
import com.app.server.exception.AppInternalException;
import com.app.server.exception.AppNotFoundException;
import com.app.server.model.User;
import com.app.server.model.companies.CompanySubscriptionPlan;
import com.app.server.repository.companies.CompanySubscriptionPlanRepository;
import com.app.server.service.CompanySubscriptionPlanService;
import com.app.server.service.UserService;
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
    private final UserService userService;


    @Override
    @CacheEvict(
            value = CACHE_NAME,
            allEntries = true
    )
    public CompanySubscriptionPlan createCompanySubscriptionPlan(
            CreateCompanySubscriptionPlanRequestDto req,
            String userSub
    ) throws AppConflicException {

        if (repository.existsByTitle(req.getTitle())) {
            throw new AppConflicException(
                    "پلنی با این عنوان قبلاً ثبت شده است"
            );
        }


        User existUser = userService.findUserBySub(userSub);

        CompanySubscriptionPlan companySubscriptionPlan = CompanySubscriptionPlan.builder()
                .createdBy(userSub)
                .createdBy(existUser.getSub())
                .title(req.getTitle())
                .description(req.getDescription())
                .completedDescription(req.getCompletedDescription())
                .tags(req.getTags())
                .active(false)
                .validDays(req.getValidDays())
                .usersCount(req.getUsersCount())
                .price(req.getPrice())
                .build();

        return repository.save(companySubscriptionPlan);
    }


    @Override
    @CachePut(
            value = CACHE_NAME,
            key = "#slug"
    )
    public CompanySubscriptionPlan updateCompanySubscriptionPlanBySlug(
            String slug,
            CreateCompanySubscriptionPlanRequestDto request
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
    public CustomResponseDto delete(String slug) {

        CompanySubscriptionPlan exist =
                findCompanySubscriptionPlanBySlug(slug);

        repository.delete(exist);

        return CustomResponseDto.builder()
                .data("")
                .status(200)
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