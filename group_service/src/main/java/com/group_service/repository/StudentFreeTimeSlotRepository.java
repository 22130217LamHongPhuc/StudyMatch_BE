package com.group_service.repository;

import com.group_service.entity.StudentFreeTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentFreeTimeSlotRepository extends JpaRepository<StudentFreeTimeSlot, Long> {
    List<StudentFreeTimeSlot> findByGroupIdAndTermId(Long groupId, Long termId);

    List<StudentFreeTimeSlot> findByGroupId(Long groupId);

    void deleteByGroupIdAndTermId(Long groupId, Long termId);
}
