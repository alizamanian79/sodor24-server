package com.app.server.service;

import com.app.server.dto.request.companies.CreateCompanySubscriptionPlanRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.exception.AppInternalException;
import com.app.server.model.companies.CompanySubscriptionPlan;

import java.util.List;

public interface CompanySubscriptionPlanService {
    CompanySubscriptionPlan createCompanySubscriptionPlan(CreateCompanySubscriptionPlanRequestDto plan , String userSub);
    CompanySubscriptionPlan updateCompanySubscriptionPlanBySlug(String slug, CreateCompanySubscriptionPlanRequestDto plan);
    CompanySubscriptionPlan findCompanySubscriptionPlanById(Long id);
    CompanySubscriptionPlan findCompanySubscriptionPlanBySlug(String slug);
    List<CompanySubscriptionPlan> findAll();
    List<CompanySubscriptionPlan> findActivePlans();
    CustomResponseDto delete(String slug);
    CompanySubscriptionPlan setActive(String slug , boolean value);
    boolean checkActivePlan(String slug) throws AppInternalException;
    CompanySubscriptionPlan findById(Long id);
}