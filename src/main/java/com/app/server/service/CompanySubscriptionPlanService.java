package com.app.server.service;

import com.app.server.dto.response.CustomResponseDto;
import com.app.server.exception.AppInternalException;
import com.app.server.model.CompanySubscriptionPlan;

import java.util.List;

public interface CompanySubscriptionPlanService {


    CompanySubscriptionPlan createCompanySubscriptionPlan(CompanySubscriptionPlan plan ,String userSub);


    CompanySubscriptionPlan updateCompanySubscriptionPlanBySlug(String slug, CompanySubscriptionPlan plan);

    CompanySubscriptionPlan findCompanySubscriptionPlanById(Long id);

    CompanySubscriptionPlan findCompanySubscriptionPlanBySlug(String slug);

    List<CompanySubscriptionPlan> findAll();

    List<CompanySubscriptionPlan> findActivePlans();

    CustomResponseDto delete(String slug);

    CompanySubscriptionPlan setActive(String slug , boolean value);

    boolean checkActivePlan(String slug) throws AppInternalException;

}