package com.example.microservice.repository;

import com.example.microservice.entity.AdminInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminInvitationRepository extends JpaRepository<AdminInvitation, Long> {
    Optional<AdminInvitation> findByTokenHash(String tokenHash);
    Optional<AdminInvitation> findByEmail(String email);
}
