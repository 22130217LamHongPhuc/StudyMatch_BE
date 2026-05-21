package com.group_service.dto;

import com.group_service.entity.enums.GroupStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateGroupStatusRequest {

    @NotNull()
    private GroupStatus status;
}

