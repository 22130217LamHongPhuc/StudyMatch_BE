package com.group_service.dto;

import com.group_service.entity.enums.GroupStudySessionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateSessionStatusRequest {

    @NotNull
    private GroupStudySessionStatus status;
}
