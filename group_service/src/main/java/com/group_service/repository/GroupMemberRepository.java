package com.group_service.repository;

import com.group_service.entity.GroupMember;
import com.group_service.entity.StudyGroup;
import com.group_service.entity.enums.GroupMemberStatus;
import com.group_service.entity.enums.GroupStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    long countByGroupId(Long groupId);

    boolean existsByGroupIdAndUserId(Long groupId, Long userId);

    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);

    boolean existsByGroupIdAndUserIdAndStatus(Long groupId, Long userId, GroupMemberStatus status);

    Optional<GroupMember> findByGroupIdAndUserIdAndStatus(Long groupId, Long userId, GroupMemberStatus status);

    List<GroupMember> findByGroupIdAndStatus(Long groupId, GroupMemberStatus status);

    long countByGroupIdAndStatus(Long groupId, GroupMemberStatus status);

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

    @Query("""
        select count(gm)
        from GroupMember gm
        join gm.studyGroup g
        where gm.userId = :userId
          and gm.status = :memberStatus
          and g.status = :groupStatus
    """)
    long countGroupsByUserId(
            @Param("userId") Long userId,
            @Param("memberStatus") GroupMemberStatus memberStatus,
            @Param("groupStatus") GroupStatus groupStatus
    );

    @Query("""
        select g
        from GroupMember gm1
        join gm1.studyGroup g
        join GroupMember gm2 on gm2.studyGroup = g
        where gm1.userId = :userId1
          and gm2.userId = :userId2
          and gm1.status = :memberStatus
          and gm2.status = :memberStatus
          and g.status = :groupStatus
    """)
    List<StudyGroup> findCommonGroups(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2,
            @Param("memberStatus") GroupMemberStatus memberStatus,
            @Param("groupStatus") GroupStatus groupStatus
    );
}
