package com.example.microservice.services.repository;

import com.example.microservice.services.entity.Unfriend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UnfriendRepo extends JpaRepository<Unfriend, Long> {
    @Query("SELECT u FROM Unfriend u WHERE (u.userId = :userId OR u.friendId = :userId) AND u.createdAt >= :cutoff")
    List<Unfriend> findRecentUnfriends(@Param("userId") Long userId, @Param("cutoff") LocalDateTime cutoff);
}
