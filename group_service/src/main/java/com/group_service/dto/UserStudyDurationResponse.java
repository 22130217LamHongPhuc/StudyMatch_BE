package com.group_service.dto;

public record UserStudyDurationResponse(
        Long userId,
        Long totalMinutes
) {
}
