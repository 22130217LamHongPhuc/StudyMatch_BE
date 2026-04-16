package com.example.microservice.repositories;
import com.example.microservice.entity.StudentFreeTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface StudentFreeTimeSlotRepository extends JpaRepository<StudentFreeTimeSlot, Long> {
    @Query("SELECT s FROM StudentFreeTimeSlot s WHERE s.userId = :userId AND s.term.termId = :termId")
    List<StudentFreeTimeSlot> findByUserIdAndTermId(@Param("userId") Long userId, @Param("termId") Long termId);
}
