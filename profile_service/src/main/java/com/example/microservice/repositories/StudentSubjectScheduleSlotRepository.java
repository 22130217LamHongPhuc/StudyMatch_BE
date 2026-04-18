package com.example.microservice.repositories;
import com.example.microservice.entity.StudentSubjectScheduleSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface StudentSubjectScheduleSlotRepository extends JpaRepository<StudentSubjectScheduleSlot, Long> {
    List<StudentSubjectScheduleSlot> findByUserIdAndTerm_TermId(Long userId, Long termId);

    List<StudentSubjectScheduleSlot> findByUserId(Long userId);
}
