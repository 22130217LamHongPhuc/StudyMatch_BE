package com.example.microservice.repositories;
import com.example.microservice.entity.StudentFreeTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface StudentFreeTimeSlotRepository extends JpaRepository<StudentFreeTimeSlot, Long> {
    List<StudentFreeTimeSlot> findByUserIdAndTerm_TermId(Long userId, Long termId);

    List<StudentFreeTimeSlot> findByUserId(Long userId);
}
