package com.app.server.service;

import com.app.server.dto.response.Sodor24ResponseDto;
import com.app.server.model.Company;
import com.app.server.model.CompanySubscriptionPlan;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CompanySubscriptionPlanService {


    CompanySubscriptionPlan createCompanySubscriptionPlan(CompanySubscriptionPlan plan ,String userSub);


    CompanySubscriptionPlan updateCompanySubscriptionPlanBySlug(String slug, CompanySubscriptionPlan plan);

    CompanySubscriptionPlan findCompanySubscriptionPlanById(Long id);

    CompanySubscriptionPlan findCompanySubscriptionPlanBySlug(String slug);

    List<CompanySubscriptionPlan> findAll();

    List<CompanySubscriptionPlan> findActivePlans();


    Sodor24ResponseDto delete(String slug);
    Sodor24ResponseDto setActive(String slug , boolean value);
}