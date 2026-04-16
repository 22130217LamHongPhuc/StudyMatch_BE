package com.example.microservice.repositories;
import com.example.microservice.entity.StudentSubjectEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface StudentSubjectEnrollmentRepository extends JpaRepository<StudentSubjectEnrollment, Long> {
    @Query("SELECT s FROM StudentSubjectEnrollment s WHERE s.userId = :userId AND s.term.termId = :termId")
    List<StudentSubjectEnrollment> findByUserIdAndTermId(@Param("userId") Long userId, @Param("termId") Long termId);
}
