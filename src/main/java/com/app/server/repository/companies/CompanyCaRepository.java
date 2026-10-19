package com.app.server.repository.companies;

import com.app.server.model.companies.CompanyCa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CompanyCaRepository extends JpaRepository<CompanyCa,Long> {

    Optional<CompanyCa> findCompanyCaBySlug(String slug);



    @Modifying
    @Query("""
        DELETE FROM CompanyCa ca
        WHERE ca.status = :status
          AND ca.isActive = false
          AND ca.createdAt <= :expireTime
    """)
    int deleteExpiredPendingCas(
            @Param("status") String status,
            @Param("expireTime") LocalDateTime expireTime
    );

}
