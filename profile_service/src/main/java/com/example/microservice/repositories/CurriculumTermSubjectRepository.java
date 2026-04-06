package com.example.microservice.repositories;


import com.example.microservice.entity.CurriculumTermSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CurriculumTermSubjectRepository extends JpaRepository<CurriculumTermSubject, Long> {

    @Query("""
        select distinct
            cts.studyYearNo as studyYearNo,
            cts.semesterNo as semesterNo
        from CurriculumTermSubject cts
        where cts.curriculum.curriculumId = :curriculumId
        order by cts.studyYearNo asc, cts.semesterNo asc
    """)
    List<StudyYearSemesterProjection> findStudyYearsAndSemestersByCurriculumId(Long curriculumId);
    List<CurriculumTermSubject> findByCurriculum_CurriculumIdAndStudyYearNoAndSemesterNoOrderByRecommendedOrderAsc(
            Long curriculumId,
            Integer studyYearNo,
            Integer semesterNo
    );
}