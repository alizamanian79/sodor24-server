package com.app.server.repository;

import com.app.server.model.UserSignature;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSignatureRepository extends JpaRepository<UserSignature, Long> {


    Optional<UserSignature> findByOtp(String otp);

    List<UserSignature> findAllByKeyId(String keyId);



    @Modifying
    @Transactional
    @Query("""
    DELETE FROM UserSignature us
    WHERE us.keyId IS NULL OR us.keyId = ''
""")
    int deleteAllInvalid();
}
