package com.group_service.repository;

import com.group_service.entity.GroupInvitation;
import com.group_service.entity.enums.GroupInvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long> {
    List<GroupInvitation> findByInviteeUserIdAndStatus(Long inviteeUserId, GroupInvitationStatus status);
    long countByInviteeUserIdAndStatus(Long inviteeUserId, GroupInvitationStatus status);
    List<GroupInvitation> findByGroupIdOrderByCreatedAtDesc(Long groupId);
    Optional<GroupInvitation> findByGroupIdAndInviteeUserIdAndStatus(Long groupId, Long inviteeUserId, GroupInvitationStatus status);
    boolean existsByGroupIdAndInviteeUserIdAndStatus(Long groupId, Long inviteeUserId, GroupInvitationStatus status);
}
