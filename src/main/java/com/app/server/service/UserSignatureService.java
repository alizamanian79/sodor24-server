package com.app.server.service;

import com.app.server.dto.request.SignatureRequest;
import com.app.server.dto.response.CustomResponseDto;
import com.app.server.dto.signatureDto.SignatureRequestDto;
import com.app.server.dto.signatureDto.SignatureResponseDto;
import com.app.server.model.UserSignature;

import java.util.List;

public interface UserSignatureService {
    UserSignature generateUserSignature(SignatureRequest req);
    List<UserSignature> findAll();
    UserSignature findById(Long id);
    UserSignature findUserSignatureByOtp(String otp);
    CustomResponseDto verifySignature(String otp) ;

    SignatureResponseDto sendSignatureRequest(SignatureRequestDto req);
    boolean callBackSignatureProcess(Long id);
}
