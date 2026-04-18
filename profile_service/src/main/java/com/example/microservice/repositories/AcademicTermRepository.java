package com.example.microservice.repositories;

import com.example.microservice.entity.AcademicTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AcademicTermRepository extends JpaRepository<AcademicTerm, Long> {
    Optional<AcademicTerm> findFirstByStatus(String status);

    List<AcademicTerm> findByAcademicYearStartBetweenOrderByAcademicYearStartAscSemesterNoAsc(
            Short startYear,
            Short endYear
    );

}
