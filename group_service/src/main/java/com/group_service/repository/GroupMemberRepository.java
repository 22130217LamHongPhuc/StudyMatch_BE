package com.group_service.repository;

import com.group_service.entity.GroupMember;
import com.group_service.entity.StudyGroup;
import com.group_service.entity.enums.GroupMemberStatus;
import com.group_service.entity.enums.GroupStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    long countByGroupId(Long groupId);

    @Query("""
        select g
        from GroupMember gm
        join gm.studyGroup g
        where gm.userId = :userId
          and gm.status = :memberStatus
          and g.status = :groupStatus
    """)
    List<StudyGroup> findGroupsByUserId(
            @Param("userId") Long userId,
            @Param("memberStatus") GroupMemberStatus memberStatus,
            @Param("groupStatus") GroupStatus groupStatus
    );
}