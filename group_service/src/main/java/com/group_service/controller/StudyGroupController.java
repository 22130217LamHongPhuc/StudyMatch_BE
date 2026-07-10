package com.group_service.controller;

import com.group_service.dto.ApiResponse;
import com.group_service.dto.CommonGroupResponse;
import com.group_service.dto.CreateStudyGroupRequest;
import com.group_service.dto.GroupMemberResponse;
import com.group_service.dto.JoinGroupRequest;
import com.group_service.dto.JoinGroupResponse;
import com.group_service.dto.StudyGroupDetailResponse;
import com.group_service.dto.StudyGroupResponse;
import com.group_service.dto.UserGroupStatsResponse;
import com.group_service.entity.enums.GroupType;
import com.group_service.entity.enums.GroupMemberStatus;
import com.group_service.enums.StatusCode;
import com.group_service.repository.GroupMemberRepository;
import com.group_service.service.StudyGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class StudyGroupController {

    private final StudyGroupService studyGroupService;
    private final GroupMemberRepository groupMemberRepository;
    private final com.group_service.clients.ChatClient chatClient;
//    private final ProfileClient profileClient;
//
//    @GetMapping("/test")
//    public Long testProfileClient() {
//        return profileClient.getActiveTerm();
//    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<StudyGroupResponse>> createGroup(
            @RequestPart("request") @Valid CreateStudyGroupRequest request,
            @RequestPart(value = "avatar", required = false) org.springframework.web.multipart.MultipartFile avatar) {
//        Long termId = profileClient.getActiveTerm();
        request.setTermId(8L);
        StudyGroupResponse response = studyGroupService.createStudyGroup(request, avatar);
        return ResponseEntity.ok(new ApiResponse<>(
               true,
               StatusCode.SUCCESS,
               "create group successfully",
                response
        ));
    }


    @PostMapping(value = "/community", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<StudyGroupResponse>> createCommunityGroup(
            @RequestPart("request") @Valid CreateStudyGroupRequest request,
            @RequestPart(value = "avatar", required = false) org.springframework.web.multipart.MultipartFile avatar) {
//        Long termId = profileClient.getActiveTerm();
        request.setTermId(8L);
        StudyGroupResponse response = studyGroupService.createCommunityGroup(request, avatar);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "create  community group successfully",
                response
        ));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<StudyGroupDetailResponse>>> getGroupsByUserId(@PathVariable Long userId) {
        List<StudyGroupDetailResponse> response = studyGroupService.getGroupsByUserId(userId);
        return ResponseEntity.ok(
                new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "create group successfully",
                response
        ));
    }

    @GetMapping("/user/{userId}/common/{otherUserId}")
    public ResponseEntity<ApiResponse<List<CommonGroupResponse>>> getCommonGroups(
            @PathVariable Long userId,
            @PathVariable Long otherUserId
    ) {
        List<CommonGroupResponse> response = studyGroupService.getCommonGroups(userId, otherUserId);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Get common groups successfully",
                response
        ));
    }

    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<ApiResponse<UserGroupStatsResponse>> getCurrentUserGroupStats(
            @PathVariable Long userId
    ) {
        UserGroupStatsResponse response = studyGroupService.getCurrentUserGroupStats(userId);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Get current user group stats successfully",
                response
        ));
    }

    @GetMapping("/browse/{userId}")
    public ResponseEntity<ApiResponse<Page<StudyGroupResponse>>> browseGroups(
            @RequestParam(required = false) GroupType type,
            @RequestParam(required = false) Long subject,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        Page<StudyGroupResponse> response = studyGroupService.getGroupsByTypeAndSubject(
                type,
                subject,
                userId,
                page,
                limit
        );

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Get groups successfully",
                response
        ));
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<ApiResponse<JoinGroupResponse>> joinGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody JoinGroupRequest request
    ) {
        JoinGroupResponse response = studyGroupService.joinGroup(groupId, request);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Join group successfully",
                response
        ));
    }

    @GetMapping("/{groupId}/members/active-user-ids")
    public ResponseEntity<ApiResponse<List<Long>>> getActiveMemberUserIds(@PathVariable Long groupId) {
        List<Long> userIds = groupMemberRepository
                .findByGroupIdAndStatus(groupId, GroupMemberStatus.ACTIVE)
                .stream()
                .map(member -> member.getUserId())
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Get active group members successfully",
                userIds
        ));
    }

    @GetMapping("/{groupId}/members/active")
    public ResponseEntity<ApiResponse<List<GroupMemberResponse>>> getActiveMembers(@PathVariable Long groupId) {
        List<GroupMemberResponse> members = groupMemberRepository
                .findByGroupIdAndStatus(groupId, GroupMemberStatus.ACTIVE)
                .stream()
                .map(member -> new GroupMemberResponse(
                        member.getUserId(),
                        member.getRole(),
                        member.getStatus(),
                        member.getJoinedAt()
                ))
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Get active group members successfully",
                members
        ));
    }

    @PostMapping("/{groupId}/members/{userId}/kick")
    public ResponseEntity<ApiResponse<Void>> kickMember(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @RequestBody(required = false) Map<String, String> request
    ) {
        String status = request == null ? "remove" : request.getOrDefault("status", "remove");
        if (!"remove".equalsIgnoreCase(status) && !"removed".equalsIgnoreCase(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status must be remove");
        }

        var member = groupMemberRepository
                .findByGroupIdAndUserIdAndStatus(groupId, userId, GroupMemberStatus.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Active group member not found"));
        member.setStatus(GroupMemberStatus.REMOVED);
        groupMemberRepository.save(member);

        try {
            chatClient.syncGroupParticipants(groupId);
        } catch (Exception e) {
            System.err.println("Failed to sync group participants on kickMember: " + e.getMessage());
        }

        String groupName = null;
        try {
            com.group_service.dto.StudyGroupDetailResponse groupDetail = studyGroupService.getGroupById(groupId);
            if (groupDetail != null) {
                groupName = groupDetail.getName();
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch group details on kickMember: " + e.getMessage());
        }

        try {
            chatClient.sendGroupKickNotification(com.group_service.dto.GroupKickNotificationRequest.builder()
                    .userId(userId)
                    .groupId(groupId)
                    .groupName(groupName)
                    .build());
        } catch (Exception e) {
            System.err.println("Failed to send WebSocket kick notification on kickMember: " + e.getMessage());
        }

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Kick member successfully",
                null
        ));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<StudyGroupDetailResponse> getGroup(@PathVariable Long groupId) {
        StudyGroupDetailResponse response = studyGroupService.getGroupById(groupId);
        return ResponseEntity.ok(response);
    }
}

