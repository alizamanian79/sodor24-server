package com.app.server.repository;

import com.app.server.model.CompanySubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanySubscriptionPlanRepository 
        extends JpaRepository<CompanySubscriptionPlan, Long> {


    List<CompanySubscriptionPlan> findAllByActiveTrue();


    boolean existsByTitle(String title);


    boolean existsByTitleAndIdNot(String title, Long id);

    boolean existsByTitleAndSlugNot(String title , String slug);

    Optional<CompanySubscriptionPlan> findCompanySubscriptionPlanBySlug(String slug);
}