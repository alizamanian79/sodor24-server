package com.app.server.repository;

import com.app.server.model.Contract;
import com.app.server.model.User;
import com.app.server.model.UserContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserContractRepository extends JpaRepository<UserContract,Long> {

    boolean existsByUserIdAndContractIdAndSignatureId(
            Long userId,
            Long contractId,
            Long signatureId
    );

    boolean existsByContractAndUser(Contract contract, User user);
    Optional<UserContract> findUserContractByContractAndUser(Contract contract, User user);
}
