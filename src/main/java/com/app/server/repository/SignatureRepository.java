package com.app.server.repository;

import com.app.server.model.Signature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface SignatureRepository extends JpaRepository<Signature, Long> {

    Optional<Signature> findSignatureById(Long id);
    void deleteBySignatureId(String signatureId);

    int deleteByExpiredAtBefore(LocalDateTime now);

    List<Signature> findSignatureByExpiredAt(LocalDateTime expiredAt);

    List<Signature> findSignatureByExpiredAtAfter(LocalDateTime expiredAtAfter);

    Optional<Signature> findSignatureBySlug(String slug);

    List<Signature> findSignatureByExpiredAtBefore(LocalDateTime now);
}
