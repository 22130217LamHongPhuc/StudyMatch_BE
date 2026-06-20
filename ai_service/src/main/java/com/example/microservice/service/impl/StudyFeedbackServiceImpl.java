package com.example.microservice.service.impl;

import com.example.microservice.dto.admin.matching.CreateStudyFeedbackRequest;
import com.example.microservice.dto.admin.matching.StudyFeedbackResponse;
import com.example.microservice.entity.StudyFeedback;
import com.example.microservice.repository.StudyFeedbackRepository;
import com.example.microservice.service.StudyFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyFeedbackServiceImpl implements StudyFeedbackService {

    private final StudyFeedbackRepository studyFeedbackRepository;

    @Override
    @Transactional
    public StudyFeedbackResponse createFeedback(CreateStudyFeedbackRequest request) {

        boolean alreadyFeedback = studyFeedbackRepository
                .existsBySessionIdAndReviewerUserId(request.getSessionId(), request.getUserId());

        if (alreadyFeedback) {
            throw new RuntimeException("User already submitted feedback for this session");
        }

        StudyFeedback feedback = new StudyFeedback();

        feedback.setSessionId(request.getSessionId());
        feedback.setReviewerUserId(request.getUserId());
        feedback.setTargetUserId(request.getTargetUserId());
        feedback.setGroupId(request.getGroupId());

        feedback.setSessionType(request.getSessionType());
        feedback.setFeedbackType(request.getFeedbackType());

        feedback.setRating(request.getRating());
        feedback.setMatchedQualityScore(request.getMatchedQualityScore());
        feedback.setCommunicationScore(request.getCommunicationScore());
        feedback.setStudyEffectivenessScore(request.getStudyEffectivenessScore());

        feedback.setEligibleForModel(Boolean.TRUE.equals(request.getEligibleForModel()));
        feedback.setComment(request.getContent());

        StudyFeedback saved = studyFeedbackRepository.save(feedback);

        return mapToResponse(saved);
    }

    private StudyFeedbackResponse mapToResponse(StudyFeedback feedback) {
        return StudyFeedbackResponse.builder()
                .id(feedback.getId())
                .sessionId(feedback.getSessionId())
                .reviewerUserId(feedback.getReviewerUserId())
                .targetUserId(feedback.getTargetUserId())
                .groupId(feedback.getGroupId())
                .sessionType(feedback.getSessionType())
                .feedbackType(feedback.getFeedbackType())
                .rating(feedback.getRating())
                .matchedQualityScore(feedback.getMatchedQualityScore())
                .communicationScore(feedback.getCommunicationScore())
                .studyEffectivenessScore(feedback.getStudyEffectivenessScore())
                .eligibleForModel(feedback.getEligibleForModel())
                .comment(feedback.getComment())
                .createdAt(feedback.getCreatedAt())
                .build();
    }
}