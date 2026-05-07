package com.group_service.dto;

public record FreeTimeSlotResponse(
        Long id,
        Long groupId,
        Long termId,
        Byte dayOfWeek,
        String slotCode,
        Boolean isAvailable
) {
}

