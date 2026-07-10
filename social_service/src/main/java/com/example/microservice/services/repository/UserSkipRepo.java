package com.example.microservice.services.repository;

import com.example.microservice.services.entity.UserSkip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserSkipRepo extends JpaRepository<UserSkip, Long> {
    boolean existsByUserIdAndSkippedUserId(Long userId, Long skippedUserId);

    @Query("SELECT us FROM UserSkip us WHERE us.userId = :userId AND us.createdAt >= :cutoff")
    List<UserSkip> findRecentSkips(@Param("userId") Long userId, @Param("cutoff") LocalDateTime cutoff);
}
