package com.app.server.service;

import com.app.server.dto.request.SignatureRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.model.Signature;

import java.util.List;

public interface SignatureService {
    Signature generateUserSignature(SignatureRequestDto req);
    List<Signature> findAll();
    Signature findById(Long id);
    Signature findUserSignatureByOtp(String otp);
    CustomResponseDto verifySignature(String otp) ;

    void sendRequestToSignatureService(Signature req);
}
