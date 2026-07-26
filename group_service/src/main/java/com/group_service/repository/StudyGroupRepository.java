package com.group_service.repository;

import com.group_service.dto.StudyGroupResponse;
import com.group_service.dto.projection.AdminGroupProjection;
import com.group_service.dto.projection.GroupStats;
import com.group_service.entity.StudyGroup;

import com.group_service.entity.enums.GroupMemberStatus;
import com.group_service.entity.enums.GroupMemberRole;
import com.group_service.entity.enums.GroupStatus;
import com.group_service.entity.enums.GroupType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

    @Query(value = """
            SELECT COALESCE(NULLIF(TRIM(g.subject_name), ''), 'Chưa phân loại') AS subject_name,
                   COUNT(DISTINCT CASE WHEN g.visibility = 'PUBLIC' THEN g.id END) AS public_count,
                   COUNT(DISTINCT CASE WHEN g.visibility = 'PRIVATE' THEN g.id END) AS private_count,
                   COUNT(DISTINCT g.id) AS total_groups,
                   COUNT(DISTINCT CASE WHEN gm.status = 'ACTIVE' THEN gm.id END) AS total_members
            FROM study_groups g
            LEFT JOIN group_members gm ON gm.group_id = g.id
            WHERE g.status = 'ACTIVE'
              AND g.visibility IN ('PUBLIC', 'PRIVATE')
            GROUP BY COALESCE(NULLIF(TRIM(g.subject_name), ''), 'Chưa phân loại')
            ORDER BY total_groups DESC, total_members DESC
            LIMIT 10
            """, nativeQuery = true)
    List<Object[]> findAdminTopSubjects();

    boolean existsByNameIgnoreCaseAndOwnerUserIdAndTermIdAndMainSubjectId(
            String name,
            Long ownerUserId,
            Long termId,
            Long mainSubjectId
    );

    @Query("""
    SELECT g.id AS id,
           g.name AS name,
           g.avatarUrl AS avatarUrl,
           g.groupType AS groupType,
           g.subjectName AS subjectName,
           g.termId AS termId,
           g.createdByUserId AS createdByUserId,
           g.ownerUserId AS ownerUserId,
           g.maxMembers AS maxMembers,
           g.status AS status,
           g.visibility AS visibility,
           g.createdAt AS createdAt,
           COUNT(gm.id) AS memberCount
    FROM StudyGroup g
    LEFT JOIN GroupMember gm
           ON gm.groupId = g.id
          AND gm.status = com.group_service.entity.enums.GroupMemberStatus.ACTIVE
          AND gm.role <> com.group_service.entity.enums.GroupMemberRole.ADMIN
    GROUP BY g.id, g.name, g.avatarUrl, g.groupType, g.subjectName,
             g.termId, g.createdByUserId, g.ownerUserId,
             g.maxMembers, g.status, g.visibility, g.createdAt
    ORDER BY g.createdAt DESC
""")
    Page<AdminGroupProjection> findAdminGroups(Pageable pageable);


    @Query("""
    SELECT 
        COUNT(g) as totalGroup,
        COALESCE(SUM(CASE WHEN g.visibility = 'COMMUNITY' THEN 1 ELSE 0 END), 0) as communityGroup,
        COALESCE(SUM(CASE WHEN g.visibility = 'PRIVATE' THEN 1 ELSE 0 END), 0) as privateGroup,
        COALESCE(SUM(CASE WHEN g.visibility = 'PUBLIC' THEN 1 ELSE 0 END), 0) as publicGroup
    FROM StudyGroup g
""")
    GroupStats getStats();




    @Query("""
SELECT g.id AS id,
       g.name AS name,
       g.avatarUrl AS avatarUrl,
       g.groupType AS groupType,
       g.subjectName AS subjectName,
       g.termId AS termId,
       g.createdByUserId AS createdByUserId,
       g.ownerUserId AS ownerUserId,
       g.maxMembers AS maxMembers,
       g.status AS status,
       g.visibility AS visibility,
       g.createdAt AS createdAt,
       COUNT(gm.id) AS memberCount
FROM StudyGroup g
LEFT JOIN GroupMember gm
           ON gm.groupId = g.id
          AND gm.status = com.group_service.entity.enums.GroupMemberStatus.ACTIVE
          AND gm.role <> com.group_service.entity.enums.GroupMemberRole.ADMIN
WHERE (CAST(:groupType AS string) IS NULL OR g.groupType = :groupType)
  AND (CAST(:groupStatus AS string) IS NULL OR g.status = :groupStatus)
  AND (
         :keyWord IS NULL OR
         LOWER(g.name) LIKE :keyWord
       )
 GROUP BY g.id, g.name, g.avatarUrl, g.groupType, g.subjectName,
          g.termId, g.createdByUserId, g.ownerUserId,
          g.maxMembers, g.status, g.visibility, g.createdAt
 ORDER BY g.createdAt DESC
""")
    Page<AdminGroupProjection> filterAdminGroups(
            @Param("groupType") GroupType groupType,
            @Param("groupStatus") GroupStatus groupStatus,
            @Param("keyWord") String keyWord,
            Pageable pageable
    );

    @Query("""
  SELECT g.id AS id,
         g.name AS name,
         g.avatarUrl AS avatarUrl,
         g.groupType AS groupType,
         g.subjectName AS subjectName,
         g.termId AS termId,
         g.createdByUserId AS createdByUserId,
         g.ownerUserId AS ownerUserId,
         g.maxMembers AS maxMembers,
         g.status AS status,
         g.visibility AS visibility,
         g.createdAt AS createdAt,
         COUNT(gm.id) AS memberCount
  FROM StudyGroup g
  LEFT JOIN GroupMember gm
            ON gm.groupId = g.id
           AND gm.status = com.group_service.entity.enums.GroupMemberStatus.ACTIVE
           AND gm.role <> com.group_service.entity.enums.GroupMemberRole.ADMIN
  WHERE LOWER(g.name) LIKE :keyWord
     OR LOWER(CAST(g.description AS string)) LIKE :keyWord
 GROUP BY g.id, g.name, g.avatarUrl, g.groupType, g.subjectName,
          g.termId, g.createdByUserId, g.ownerUserId,
          g.maxMembers, g.status, g.visibility, g.createdAt
""")
    Page<AdminGroupProjection> searchAdminGroupsByKeyword(
            @Param("keyWord") String keyWord,
            Pageable pageable
    );

    @Query("""
    SELECT new com.group_service.dto.StudyGroupResponse(
        g.id,
        g.name,
        g.avatarUrl,
        g.description,
        g.ownerUserId,
        g.termId,
        g.mainSubjectId,
        g.subjectName,
        g.maxMembers,
        g.visibility,
        g.status,
        g.createdAt,
        g.updatedAt,
        COUNT(memberCount.id),
        CASE WHEN EXISTS (
            SELECT 1
            FROM GroupMember gm
            WHERE gm.groupId = g.id
              AND gm.userId = :currentUserId
              AND gm.status IN :memberStatuses
        ) THEN true ELSE false END,
        CASE WHEN EXISTS (
            SELECT 1
            FROM GroupInvitation gi
            WHERE gi.groupId = g.id
              AND gi.inviteeUserId = :currentUserId
              AND gi.status = com.group_service.entity.enums.GroupInvitationStatus.PENDING
        ) THEN true ELSE false END
    )
    FROM StudyGroup g
    LEFT JOIN GroupMember memberCount
           ON memberCount.groupId = g.id
          AND memberCount.status = :activeMemberStatus
          AND memberCount.role <> :excludedMemberRole
    WHERE g.status = :status
      AND g.visibility <> com.group_service.entity.enums.GroupVisibility.PRIVATE
      AND (:groupType IS NULL OR g.groupType = :groupType)
      AND (:mainSubjectId IS NULL OR g.mainSubjectId = :mainSubjectId)
      AND (
          :keyword IS NULL
          OR LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(COALESCE(g.subjectName, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(CAST(g.description AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
    GROUP BY g.id, g.name, g.avatarUrl, g.description, g.ownerUserId, g.termId,
             g.mainSubjectId, g.subjectName, g.maxMembers, g.visibility, g.status,
             g.createdAt, g.updatedAt
""")
    Page<StudyGroupResponse> findByFiltersForBrowse(
            @Param("status") GroupStatus status,
            @Param("groupType") GroupType groupType,
            @Param("mainSubjectId") Long mainSubjectId,
            @Param("keyword") String keyword,
            @Param("currentUserId") Long currentUserId,
            @Param("memberStatuses") Collection<GroupMemberStatus> memberStatuses,
            @Param("activeMemberStatus") GroupMemberStatus activeMemberStatus,
            @Param("excludedMemberRole") GroupMemberRole excludedMemberRole,
            Pageable pageable
    );
}

