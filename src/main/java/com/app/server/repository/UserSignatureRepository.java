package com.app.server.repository;

import com.app.server.model.UserSignature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSignatureRepository extends JpaRepository<UserSignature, Long> {


    Optional<UserSignature> findByOtp(String otp);
}
