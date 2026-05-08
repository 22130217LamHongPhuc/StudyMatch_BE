package com.example.microservice.repository;

import com.example.microservice.entity.PasswordResetToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    @Modifying
    @Transactional
    @Query("""
    UPDATE PasswordResetToken p
    SET p.used = true
    WHERE p.userId = :userId
      AND p.used = false
""")
    void disableAllValidTokens(@Param("userId") Long userId);
}
