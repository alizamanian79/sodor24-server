package com.app.server.repository;

import com.app.server.model.UserCa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCaRepository extends JpaRepository<UserCa, Long> {
    Optional<UserCa> getUserCaBySlug(String slug);
    List<UserCa> getUserCaByUser_Sub(String userSub);
}
