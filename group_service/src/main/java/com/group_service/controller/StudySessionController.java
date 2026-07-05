package com.group_service.controller;

import com.group_service.dto.*;
import com.group_service.entity.enums.GroupStudySessionStatus;
import com.group_service.entity.enums.StudySessionParticipantStatus;
import com.group_service.entity.enums.StudySessionType;
import com.group_service.enums.StatusCode;
import com.group_service.service.StudySessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

//@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/study-sessions")
@RequiredArgsConstructor
public class StudySessionController {

    private final StudySessionService studySessionService;

    @PostMapping("/group/{groupId}")
    public ResponseEntity<ApiResponse<StudySessionResponse>> createGroupSession(
            @PathVariable Long groupId,
            @Valid @RequestBody CreateStudySessionRequest request
    ) {
        StudySessionResponse response = studySessionService.createSession(groupId, request);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Create group study session successfully",
                response
        ));
    }

    @PostMapping("/pair")
    public ResponseEntity<ApiResponse<StudySessionResponse>> createPairSession(
            @Valid @RequestBody CreateStudySessionRequest request
    ) {
        StudySessionResponse response = studySessionService.createPairSession(request);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Create pair study session successfully",
                response
        ));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<StudySessionResponse>>> getSessionsByUser(
            @PathVariable Long userId,
            @RequestParam(required = false) StudySessionType sessionType,
            @RequestParam(required = false) StudySessionParticipantStatus participantStatus,
            @RequestParam(required = false) GroupStudySessionStatus sessionStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startTime"));
        Page<StudySessionResponse> sessions = studySessionService.getSessionsByUserId(
                userId, sessionType, participantStatus, sessionStatus, startFrom, startTo, pageable
        );
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Get sessions successfully",
                sessions
        ));
    }

    @GetMapping("/user/{userId}/upcoming")
    public ResponseEntity<ApiResponse<List<StudySessionResponse>>> getTopUpcomingSessions(
            @PathVariable Long userId
    ) {
        List<StudySessionResponse> sessions = studySessionService.getTopUpcomingSessions(userId);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Get upcoming sessions successfully",
                sessions
        ));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<ApiResponse<List<StudySessionResponse>>> getSessionsByGroup(
            @PathVariable Long groupId,
            @RequestParam(required = false) Long userId
    ) {
        List<StudySessionResponse> sessions = studySessionService.getSessionsByGroupId(groupId, userId);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Get group sessions successfully",
                sessions
        ));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<StudySessionResponse>> getSessionById(
            @PathVariable Long sessionId,
            @RequestParam Long userId
    ) {
        StudySessionResponse response = studySessionService.getSessionById(sessionId, userId);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Get session detail successfully",
                response
        ));
    }

    @PostMapping("/{sessionId}/join")
    public ResponseEntity<ApiResponse<JoinStudySessionResponse>> joinSession(
            @PathVariable Long sessionId,
            @RequestParam Long userId
    ) {
        JoinStudySessionResponse response = studySessionService.joinSession(sessionId, userId);

        System.out.println(response);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Join session successfully",
                response
        ));
    }

    @PostMapping("/{sessionId}/leave")
    public ResponseEntity<ApiResponse<LeaveStudySessionResponse>> leaveSession(
            @PathVariable Long sessionId,
            @Valid @RequestBody(required = false) LeaveStudySessionRequest request
    ) {
        LeaveStudySessionResponse response = studySessionService.leaveSession(sessionId, request.getUserId(), request);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Leave session successfully",
                response
        ));
    }


    @GetMapping("/{sessionId}/feedback-eligibility")
    public ResponseEntity<ApiResponse<FeedbackEligibilityResponse>> getFeedbackEligibility(
            @PathVariable Long sessionId,
            @RequestParam Long userId
    ) {
        FeedbackEligibilityResponse response = studySessionService.getFeedbackEligibility(sessionId, userId);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Get feedback eligibility successfully",
                response
        ));
    }


    @PostMapping("/{sessionId}/attendance/auto-close")
    public ResponseEntity<ApiResponse<Void>> autoCloseAttendanceLogs(
            @PathVariable Long sessionId
    ) {
        studySessionService.autoCloseAttendanceLogs(sessionId);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Auto close attendance logs successfully",
                null
        ));
    }

    @GetMapping("/{sessionId}/confirmation-stats")
    public ResponseEntity<ApiResponse<SessionConfirmationStatsResponse>> getConfirmationStats(
            @PathVariable Long sessionId,
            @RequestParam Long userId
    ) {
        SessionConfirmationStatsResponse response = studySessionService.getConfirmationStats(sessionId, userId);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Get confirmation stats successfully",
                response
        ));
    }

    @PatchMapping("/{sessionId}/participants/{userId}/respond")
    public ResponseEntity<ApiResponse<StudySessionResponse>> respondToSession(
            @PathVariable Long sessionId,
            @PathVariable Long userId,
            @Valid @RequestBody RespondSessionRequest request
    ) {
        StudySessionResponse response = studySessionService.respondToSession(sessionId, userId, request.getStatus());
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Respond to session successfully",
                response
        ));
    }

    @PatchMapping("/{sessionId}/status")
    public ResponseEntity<ApiResponse<StudySessionResponse>> updateSessionStatus(
            @PathVariable Long sessionId,
            @Valid @RequestBody UpdateSessionStatusRequest request,
            @RequestParam Long userId
    ) {
        StudySessionResponse response = studySessionService.updateSessionStatus(sessionId, userId, request.getStatus());
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Update session status successfully",
                response
        ));
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<Void>> cancelSession(
            @PathVariable Long sessionId,
            @RequestParam Long userId
    ) {
        studySessionService.cancelSession(sessionId, userId);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Cancel session successfully",
                null
        ));
    }

    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<ApiResponse<StudySessionStatsResponse>> getSessionStats(
            @PathVariable Long userId
    ) {
        StudySessionStatsResponse stats = studySessionService.getSessionStats(userId);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Get session stats successfully",
                stats
        ));
    }



}
