package com.example.microservice.repository;

import com.example.microservice.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    User findUsersByUserId(Long userId);
    List<User> findAllByUserIdIn(List<Long> userIds);



    @Query("""
        SELECT u
        FROM User u
        WHERE
            (
                :keyword IS NULL
                OR :keyword = ''
                OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            AND
            (
                :status IS NULL
                OR :status = ''
                OR LOWER(u.status) = LOWER(:status)
            )
            AND
            (
                :role IS NULL
                OR :role = ''
                OR LOWER(u.role) = LOWER(:role)
            )
            AND
            (
                :excludeUserId IS NULL
                OR u.userId <> :excludeUserId
            )
    """)
    Page<User> findUsersForAdmin(
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("role") String role,
            @Param("excludeUserId") Long excludeUserId,
            Pageable pageable
    );}

