package com.app.server.service;

import com.app.server.dto.response.CustomResponseDto;
import com.app.server.model.Signature;

import java.util.List;
import java.util.UUID;

public interface SignatureService {
    Signature generateSignature(Long userId , Signature signature);
    List<Signature> signatureList();
    Signature getSignatureById(Long signatureId);
    Object deleteSignatureById(Long signatureId);

    boolean useSignature(String signatureSlug , int count);
    Signature findSignatureByIdSlug(String slug);
}
