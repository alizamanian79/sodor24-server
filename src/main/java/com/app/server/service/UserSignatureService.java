package com.app.server.service;

import com.app.server.model.UserSignature;

import java.util.List;

public interface UserSignatureService {
    UserSignature generateUserSignature(Long userId , Long signatureId);
    List<UserSignature> findAll();
    UserSignature findById(Long id);
}
