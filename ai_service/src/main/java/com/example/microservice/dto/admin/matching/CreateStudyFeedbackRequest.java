package com.example.microservice.dto.admin.matching;

import com.example.microservice.enums.StudyFeedbackType;
import com.example.microservice.enums.StudySessionType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStudyFeedbackRequest {

    private Long sessionId;
    private Long userId;
    private Long targetUserId;
    private Long groupId;

    private StudySessionType sessionType;
    private StudyFeedbackType feedbackType;

    private String content;
    private Boolean eligibleForModel;

    private Integer rating;
    private Integer matchedQualityScore;
    private Integer communicationScore;
    private Integer studyEffectivenessScore;
}