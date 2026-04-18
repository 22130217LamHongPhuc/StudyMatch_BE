package com.example.microservice.repositories;
import com.example.microservice.entity.StudentSubjectEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface StudentSubjectEnrollmentRepository extends JpaRepository<StudentSubjectEnrollment, Long> {
    List<StudentSubjectEnrollment> findByUserIdAndTerm_TermId(Long userId, Long termId);

    List<StudentSubjectEnrollment> findByUserId(Long userId);
}
