package com.app.server.repository;

import com.app.server.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {


    @Query("""
        SELECT o FROM Otp o
        WHERE o.user.phoneNumber = :phoneNumber
          AND o.used       = false
          AND o.expiresAt  > :now
        ORDER BY o.createdAt DESC
        LIMIT 1
    """)
    Optional<Otp> findActiveOtpByPhoneNumber(
            @Param("phoneNumber") String phoneNumber,
            @Param("now")   LocalDateTime now
    );


    @Modifying
    @Query("DELETE FROM Otp o WHERE o.expiresAt < :now")
    void deleteExpiredOtps(@Param("now") LocalDateTime now);


    @Modifying
    @Query("DELETE FROM Otp o WHERE o.user.phoneNumber = :phoneNumber")
    void deleteAllByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}