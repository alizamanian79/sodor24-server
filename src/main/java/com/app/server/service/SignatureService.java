package com.app.server.service;

import com.app.server.dto.request.SignatureRequestDto;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.exception.AppBadRequestException;
import com.app.server.model.Signature;

import java.util.List;

public interface SignatureService {
    Signature generateSignature(SignatureRequestDto req) ;
    List<Signature> findAll();
    Signature findById(Long id);
    Signature findSignatureByOtp(String otp) ;
    CustomResponseDto verifySignature(String otp) ;
    CustomResponseDto deleteSignature(Long id);
    Signature updateSignature(Long id , SignatureRequestDto req);

    Signature changeSignatureValid(Long id,boolean valid);
    void sendRequestToSignatureService(Signature req);
    boolean useSignature(Signature req) throws Exception;
}
