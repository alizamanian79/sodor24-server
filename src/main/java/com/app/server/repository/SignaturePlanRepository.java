package com.app.server.repository;

import com.app.server.model.SignaturePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface SignaturePlanRepository extends JpaRepository<SignaturePlan, Long>, JpaSpecificationExecutor<SignaturePlan> {
    Optional<SignaturePlan> findSignatureById(Long id);
}
