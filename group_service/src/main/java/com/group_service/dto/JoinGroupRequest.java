package com.group_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinGroupRequest {

    @NotNull()
    private Long userId;
}

