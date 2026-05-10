package com.app.server.repository;

import com.app.server.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findContractById(Long id);
    Optional<Contract> findContractBySlug(String slug);

    boolean existsBySlug(String slug);
}
