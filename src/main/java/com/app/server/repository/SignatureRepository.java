package com.app.server.repository;

import com.app.server.model.Signature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SignatureRepository extends JpaRepository<Signature, Long> {
 Optional<Signature> findByOtp(String otp);
 Optional<Signature> findSignatureById(Long id);
 Optional<Signature> findSignatureByOtp(String otp);

 @Transactional
 @Modifying
 @Query("DELETE FROM Signature s WHERE s.id = :id")
 void deleteSignatureById(@Param("id") Long id);

 List<Signature> getSignatureByValid(boolean valid);

 List<Signature> findSignatureByValid(boolean valid);

 List<Signature> findSignatureByVerified(boolean verified);
}
