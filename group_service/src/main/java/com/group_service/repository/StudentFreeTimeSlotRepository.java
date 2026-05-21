package com.group_service.repository;

import com.group_service.entity.UserFreeTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentFreeTimeSlotRepository extends JpaRepository<UserFreeTimeSlot, Long> {
    List<UserFreeTimeSlot> findByGroupIdAndTermId(Long groupId, Long termId);

    List<UserFreeTimeSlot> findByGroupId(Long groupId);

    void deleteByGroupIdAndTermId(Long groupId, Long termId);
}
