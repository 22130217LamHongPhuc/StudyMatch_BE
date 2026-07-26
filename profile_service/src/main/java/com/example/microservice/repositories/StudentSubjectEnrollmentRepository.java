package com.example.microservice.repositories;

import com.example.microservice.entity.StudentSubjectEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface StudentSubjectEnrollmentRepository extends JpaRepository<StudentSubjectEnrollment, Long> {
    List<StudentSubjectEnrollment> findByUserIdAndTerm_TermId(Long userId, Long termId);

    List<StudentSubjectEnrollment> findByUserId(Long userId);

    @Query("""
        select e.subject.subjectName, count(e.id) as countVal
        from StudentSubjectEnrollment e
        group by e.subject.subjectName
        order by countVal desc
    """)
    List<Object[]> getTopSubjectsEnrollment();

    boolean existsBySubject_SubjectId(Long subjectId);

    boolean existsByTerm_TermId(Long termId);
}
