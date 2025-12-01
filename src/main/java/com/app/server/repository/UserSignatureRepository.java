package com.app.server.repository;

import com.app.server.model.UserSignature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSignatureRepository extends JpaRepository<UserSignature, Long> {


}
