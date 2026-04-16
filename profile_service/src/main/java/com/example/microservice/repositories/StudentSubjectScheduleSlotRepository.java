package com.example.microservice.repositories;
import com.example.microservice.entity.StudentSubjectScheduleSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface StudentSubjectScheduleSlotRepository extends JpaRepository<StudentSubjectScheduleSlot, Long> {
    @Query("SELECT s FROM StudentSubjectScheduleSlot s WHERE s.userId = :userId AND s.term.termId = :termId")
    List<StudentSubjectScheduleSlot> findByUserIdAndTermId(@Param("userId") Long userId, @Param("termId") Long termId);
}
