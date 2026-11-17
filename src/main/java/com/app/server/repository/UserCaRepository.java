package com.app.server.repository;

import com.app.server.model.UserCa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCaRepository extends JpaRepository<UserCa, Long> {


}
