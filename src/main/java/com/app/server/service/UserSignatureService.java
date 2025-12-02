package com.app.server.service;

import com.app.server.dto.request.SignatureRequest;
import com.app.server.model.UserSignature;

import java.util.List;

public interface UserSignatureService {
    UserSignature generateUserSignature(SignatureRequest req);
    List<UserSignature> findAll();
    UserSignature findById(Long id);


    boolean callBackSignatureProcess(Long id);

    Object verifySignature(String otp);

}
