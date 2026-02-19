package com.app.server.repository;

import com.app.server.model.Signature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SignatureRepository extends JpaRepository<Signature, Long> {
 Optional<Signature> findByOtp(String otp);
}
