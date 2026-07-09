package com.group_service.repository;

import com.group_service.dto.StudyGroupResponse;
import com.group_service.dto.projection.AdminGroupProjection;
import com.group_service.dto.projection.GroupStats;
import com.group_service.entity.StudyGroup;

import com.group_service.entity.enums.GroupMemberStatus;
import com.group_service.entity.enums.GroupStatus;
import com.group_service.entity.enums.GroupType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

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
WHERE (CAST(:groupType AS string) IS NULL OR g.groupType = :groupType)
  AND (CAST(:groupStatus AS string) IS NULL OR g.status = :groupStatus)
  AND (
        :keyWord IS NULL OR
        LOWER(g.name) LIKE LOWER(CONCAT('%', :keyWord, '%'))
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
 WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :keyWord, '%'))
    OR LOWER(CAST(g.description AS string)) LIKE LOWER(CONCAT('%', :keyWord, '%'))
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
        CASE WHEN EXISTS (
            SELECT 1
            FROM GroupMember gm
            WHERE gm.groupId = g.id
              AND gm.userId = :currentUserId
              AND gm.status IN :memberStatuses
        ) THEN true ELSE false END
    )
    FROM StudyGroup g
    WHERE g.status = :status
      AND g.visibility <> com.group_service.entity.enums.GroupVisibility.PRIVATE
      AND (:groupType IS NULL OR g.groupType = :groupType)
      AND (:mainSubjectId IS NULL OR g.mainSubjectId = :mainSubjectId)
""")
    Page<StudyGroupResponse> findByFiltersForBrowse(
            @Param("status") GroupStatus status,
            @Param("groupType") GroupType groupType,
            @Param("mainSubjectId") Long mainSubjectId,
            @Param("currentUserId") Long currentUserId,
            @Param("memberStatuses") Collection<GroupMemberStatus> memberStatuses,
            Pageable pageable
    );
}

