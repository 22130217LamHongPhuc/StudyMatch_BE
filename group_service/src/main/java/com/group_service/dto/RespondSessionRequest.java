package com.group_service.dto;

import com.group_service.entity.enums.StudySessionParticipantStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RespondSessionRequest {

    @NotNull
    private StudySessionParticipantStatus status;
}
