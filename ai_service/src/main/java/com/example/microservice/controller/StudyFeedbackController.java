package com.example.microservice.controller;

import com.example.microservice.dto.admin.matching.ApiResponse;
import com.example.microservice.dto.admin.matching.CreateStudyFeedbackRequest;
import com.example.microservice.dto.admin.matching.StudyFeedbackResponse;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.service.StudyFeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/study-feedbacks")
@RequiredArgsConstructor
public class StudyFeedbackController {

    private final StudyFeedbackService studyFeedbackService;

    @PostMapping
    public ResponseEntity<ApiResponse<StudyFeedbackResponse>> createFeedback(
            @Valid @RequestBody CreateStudyFeedbackRequest request
    ) {
        StudyFeedbackResponse response = studyFeedbackService.createFeedback(request);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Create study feedback successfully",
                response
        ));
    }

    @GetMapping("/session/{sessionId}/user/{userId}")
    public ResponseEntity<ApiResponse<StudyFeedbackResponse>> getFeedbackBySessionAndUser(
            @PathVariable Long sessionId,
            @PathVariable Long userId
    ) {
        StudyFeedbackResponse response = studyFeedbackService.getFeedbackBySessionAndUser(sessionId, userId);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Get study feedback successfully",
                response
        ));
    }
}