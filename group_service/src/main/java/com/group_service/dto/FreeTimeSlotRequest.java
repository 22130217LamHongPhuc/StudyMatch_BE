package com.group_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FreeTimeSlotRequest{
        @Min(0)
        @Max(6)
        Byte dayOfWeek;

        @NotBlank
        String slotCode;

        Boolean isAvailable;
}
