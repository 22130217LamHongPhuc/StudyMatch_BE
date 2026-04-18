package com.example.microservice.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FreeTimeSlotResponse {
    // Getters and Setters
    private Long id;
    private Byte dayOfWeek;
    private String slotCode;
    private Boolean isAvailable;

    public FreeTimeSlotResponse() {
    }

    public FreeTimeSlotResponse(Long id, Byte dayOfWeek, String slotCode, Boolean isAvailable) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.slotCode = slotCode;
        this.isAvailable = isAvailable;
    }

}

