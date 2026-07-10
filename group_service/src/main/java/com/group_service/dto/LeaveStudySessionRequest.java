package com.group_service.dto;

import com.group_service.entity.enums.StudySessionLeaveReason;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveStudySessionRequest {

    private StudySessionLeaveReason leaveReason;
    private Long userId;
}
