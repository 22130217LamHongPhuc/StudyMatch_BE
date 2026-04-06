package com.example.microservice.repositories;

import com.example.microservice.entity.AcademicTerm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcademicTermRepository extends JpaRepository<AcademicTerm, Long> {
    Optional<AcademicTerm> findFirstByStatus(String status);
}
