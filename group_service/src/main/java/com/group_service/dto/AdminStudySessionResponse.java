package com.group_service.dto;

import com.group_service.entity.enums.GroupStudySessionMode;
import com.group_service.entity.enums.GroupStudySessionStatus;
import com.group_service.entity.enums.StudySessionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStudySessionResponse {
    private Long id;
    private String title;
    private String subjectName;
    private String groupName;
    private String groupAvatarUrl;
    private StudySessionType sessionType;
    private String creatorName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private GroupStudySessionMode studyMode;
    private Long membersCount;
    private Integer maxMembers;
    private GroupStudySessionStatus status;
}
