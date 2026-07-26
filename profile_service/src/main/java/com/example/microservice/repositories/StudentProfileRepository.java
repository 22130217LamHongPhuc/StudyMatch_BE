package com.example.microservice.repositories;

import com.example.microservice.entity.StudentProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByStudentCode(String studentCode);
    Optional<StudentProfile> findByUserId(Long userId);

    @Query("select p.cohort.cohortCode, count(p.profileId) from StudentProfile p group by p.cohort.cohortCode")
    List<Object[]> countStudentsPerCohort();

    @Query("select p.region, count(p.profileId) from StudentProfile p group by p.region")
    List<Object[]> countStudentsPerRegion();

    @Query("""
        select p from StudentProfile p
        where (:search is null or :search = '' or lower(p.fullName) like lower(concat('%', :search, '%')) or lower(p.studentCode) like lower(concat('%', :search, '%')))
          and (:cohortId is null or p.cohort.cohortId = :cohortId)
    """)
    Page<StudentProfile> searchProfiles(@Param("search") String search, @Param("cohortId") Long cohortId, Pageable pageable);

    boolean existsByCohort_CohortId(Long cohortId);
}
