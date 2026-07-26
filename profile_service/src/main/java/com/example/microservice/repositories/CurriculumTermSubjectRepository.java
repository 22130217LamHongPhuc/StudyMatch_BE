package com.example.microservice.repositories;

import com.example.microservice.dto.response.SubjectInfoResponse;
import com.example.microservice.entity.CurriculumTermSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
        select distinct new com.example.microservice.dto.response.SubjectInfoResponse(
            s.subjectId,
            s.subjectCode,
            s.subjectName
        )
        from CurriculumTermSubject cts
        join cts.subject s
        where cts.curriculum.curriculumId = :curriculumId
        order by s.subjectName asc
    """)
    List<SubjectInfoResponse> findDistinctSubjectsByCurriculumId(@Param("curriculumId") Long curriculumId);

    List<CurriculumTermSubject> findByCurriculum_CurriculumId(Long curriculumId);

    boolean existsBySubject_SubjectId(Long subjectId);

    boolean existsByCurriculum_CurriculumId(Long curriculumId);

    void deleteByCurriculum_CurriculumIdAndSubject_SubjectId(Long curriculumId, Long subjectId);
}
