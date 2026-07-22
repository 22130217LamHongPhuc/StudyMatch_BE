package com.group_service.dto;

import com.group_service.entity.enums.StudySessionParticipantStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class RespondMultipleSessionsRequest {

    @NotEmpty
    private List<Long> sessionIds;

    @NotNull
    private StudySessionParticipantStatus status;
}
