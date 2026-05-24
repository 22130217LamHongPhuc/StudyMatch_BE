package com.group_service.repository;

import com.group_service.entity.GroupFreeTimeSlot;
import com.group_service.entity.UserFreeTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupFreeTimeSlotRepository extends JpaRepository<GroupFreeTimeSlot, Long> {
    List<GroupFreeTimeSlot> findByGroupIdAndTermId(Long groupId, Long termId);

    List<GroupFreeTimeSlot> findByGroupId(Long groupId);

    void deleteByGroupIdAndTermId(Long groupId, Long termId);
}
