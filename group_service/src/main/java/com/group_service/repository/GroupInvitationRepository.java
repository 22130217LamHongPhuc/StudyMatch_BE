package com.group_service.repository;

import com.group_service.entity.GroupInvitation;
import com.group_service.entity.enums.GroupInvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long> {
    @Query("SELECT gi FROM GroupInvitation gi WHERE gi.groupId IN :groupIds AND gi.status = :status AND gi.inviterUserId = gi.inviteeUserId ORDER BY gi.createdAt DESC")
    List<GroupInvitation> findPendingJoinRequestsByGroupIds(
            @Param("groupIds") List<Long> groupIds,
            @Param("status") GroupInvitationStatus status
    );

    List<GroupInvitation> findByInviteeUserIdAndStatus(Long inviteeUserId, GroupInvitationStatus status);
    List<GroupInvitation> findByInviterUserIdAndInviteeUserIdAndStatusOrderByCreatedAtDesc(Long inviterUserId, Long inviteeUserId, GroupInvitationStatus status);
    long countByInviteeUserIdAndStatus(Long inviteeUserId, GroupInvitationStatus status);
    List<GroupInvitation> findByGroupIdOrderByCreatedAtDesc(Long groupId);
    Optional<GroupInvitation> findByGroupIdAndInviteeUserIdAndStatus(Long groupId, Long inviteeUserId, GroupInvitationStatus status);
    boolean existsByGroupIdAndInviteeUserIdAndStatus(Long groupId, Long inviteeUserId, GroupInvitationStatus status);
}
