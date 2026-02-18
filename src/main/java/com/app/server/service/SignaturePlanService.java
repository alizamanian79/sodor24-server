package com.app.server.service;

import com.app.server.model.SignaturePlan;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SignaturePlanService {

    List<SignaturePlan> getAllSignaturePlans();
    SignaturePlan findSignaturePlanById(Long id);
    SignaturePlan generateSignaturePlan(SignaturePlan signature);
    Object deleteSignaturePlan(Long signatureId);
    SignaturePlan updateSignaturePlan(SignaturePlan signature);


    Page<SignaturePlan> getPageableSignaturesPlan(Integer page, Integer size, String search, String sortBy, String sortDir);
    Object activeSignaturePlan(Long signatureId , boolean active);


}
