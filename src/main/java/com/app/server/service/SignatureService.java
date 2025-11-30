package com.app.server.service;

import com.app.server.model.Signature;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SignatureService {

    List<Signature> getSignatures();
    Signature findSignatureById(Long id);
    Page<Signature> getPageableSignatures(
            Integer page,
            Integer size,
            String search,
            String sortBy,
            String sortDir
    );
    Signature generateSignature(Signature signature);

    Object deleteSignature(Long signatureId);
    Signature updateSignature(Signature signature);
    Object activeSignature(Long signatureId , boolean active);


}
