package com.example.microservice.dto.admin.matching;

import com.example.microservice.enums.StudySessionType;
import java.time.LocalDateTime;

import com.example.microservice.enums.StudyFeedbackType;
import com.example.microservice.enums.StudySessionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyFeedbackResponse {

    private Long id;

    private Long sessionId;

    private Long reviewerUserId;

    private Long targetUserId;

    private Long groupId;

    private StudySessionType sessionType;

    private StudyFeedbackType feedbackType;

    private Integer rating;

    private Integer matchedQualityScore;

    private Integer communicationScore;

    private Integer studyEffectivenessScore;

    private Boolean eligibleForModel;

    private String comment;

    private LocalDateTime createdAt;
}
