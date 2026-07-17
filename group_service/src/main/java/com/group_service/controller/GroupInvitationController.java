package com.group_service.controller;

import com.group_service.dto.ApiResponse;
import com.group_service.dto.GroupInvitationResponse;
import com.group_service.dto.JoinGroupRequest;
import com.group_service.enums.StatusCode;
import com.group_service.service.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupInvitationController {

    private final StudyGroupService studyGroupService;

    @PostMapping("/{groupId}/invitations")
    public ResponseEntity<ApiResponse<GroupInvitationResponse>> inviteMember(
            @PathVariable Long groupId,
            @RequestBody @jakarta.validation.Valid JoinGroupRequest request,
            @RequestHeader("Authorization") String token
    ) {
        GroupInvitationResponse response = studyGroupService.sendInvitation(groupId, request.getUserId(), request.getMessage(), token);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Invite member successfully",
                response
        ));
    }

    @GetMapping("/{groupId}/invitations")
    public ResponseEntity<ApiResponse<List<GroupInvitationResponse>>> getGroupInvitations(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String token
    ) {
        List<GroupInvitationResponse> response = studyGroupService.getGroupInvitations(groupId, token);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Get group invitations successfully",
                response
        ));
    }
    @GetMapping("/invitations/pending")
    public ResponseEntity<ApiResponse<List<GroupInvitationResponse>>> getPendingInvitations(
            @RequestHeader("Authorization") String token
    ) {
        List<GroupInvitationResponse> response = studyGroupService.getPendingInvitations(token);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Get pending invitations successfully",
                response
        ));
    }

    @GetMapping("/invitations/sent-pending")
    public ResponseEntity<ApiResponse<List<GroupInvitationResponse>>> getSentPendingJoinRequests(
            @RequestHeader("Authorization") String token
    ) {
        List<GroupInvitationResponse> response = studyGroupService.getSentPendingJoinRequests(token);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Get sent pending join requests successfully",
                response
        ));
    }

    @PostMapping("/invitations/{invitationId}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptInvitation(
            @PathVariable Long invitationId,
            @RequestHeader("Authorization") String token
    ) {
        studyGroupService.acceptInvitation(invitationId, token);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Accept invitation successfully",
                null
        ));
    }

    @PostMapping("/invitations/{invitationId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectInvitation(
            @PathVariable Long invitationId,
            @RequestHeader("Authorization") String token
    ) {
        studyGroupService.rejectInvitation(invitationId, token);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Reject invitation successfully",
                null
        ));
    }
}

