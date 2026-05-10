package com.group_service.repository;

import com.group_service.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

    boolean existsByNameIgnoreCaseAndOwnerUserIdAndTermIdAndMainSubjectId(
            String name,
            Long ownerUserId,
            Long termId,
            Long mainSubjectId
    );
}

