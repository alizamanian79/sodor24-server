package com.app.server.repository;

import com.app.server.model.UserContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserContractRepository extends JpaRepository<UserContract, Long> {
}
