package com.example.microservice.repositories;


import com.example.microservice.entity.Cohort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CohortRepository extends JpaRepository<Cohort, Long> {

    Optional<Cohort> findByCohortCode(String cohortCode);

}