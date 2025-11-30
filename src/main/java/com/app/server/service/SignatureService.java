package com.app.server.service;

import com.app.server.dto.request.SignatureRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.model.Signature;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

public interface SignatureService {

    Object generateSignature(SignatureRequestDto req) throws JsonProcessingException;
    List<Signature> signatureList();
    Signature getSignatureById(Long signatureId);
    Object deleteSignatureById(Long signatureId);
    Signature chargeSignature(String slug);
    boolean useSignature(String signatureSlug , int count);
    Signature findSignatureByIdSlug(String slug);


    boolean activeSignature(String slug) throws JsonProcessingException;
}
