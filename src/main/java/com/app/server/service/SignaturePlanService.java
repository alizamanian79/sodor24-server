package com.app.server.service;

import com.app.server.dto.request.SignaturePlanRequestDto;
import com.app.server.model.SignaturePlan;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SignaturePlanService {

    List<SignaturePlan> getAllSignaturePlans();
    SignaturePlan findSignaturePlanById(Long id);
    SignaturePlan generateSignaturePlan(SignaturePlanRequestDto req);
    Object deleteSignaturePlan(Long id);
    SignaturePlan updateSignaturePlanById(SignaturePlanRequestDto req,Long id);


    Page<SignaturePlan> getPageableSignaturesPlan(Integer page, Integer size, String search, String sortBy, String sortDir);
    Object activeSignaturePlan(Long id , boolean active);


}
