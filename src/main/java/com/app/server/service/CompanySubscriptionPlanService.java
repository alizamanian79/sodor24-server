package com.app.server.service;

import com.app.server.dto.response.Sodor24ResponseDto;
import com.app.server.exception.AppInternalException;
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

    CompanySubscriptionPlan setActive(String slug , boolean value);

    boolean checkActivePlan(String slug) throws AppInternalException;

}