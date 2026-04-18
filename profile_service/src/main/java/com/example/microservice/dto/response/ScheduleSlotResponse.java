package com.example.microservice.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ScheduleSlotResponse {
    // Getters and Setters
    private Long id;
    private SubjectInfoResponse subject;
    private Byte dayOfWeek;
    private String slotCode;
    private String scheduleType;
    private String location;
    private String note;

    public ScheduleSlotResponse() {
    }

    public ScheduleSlotResponse(Long id, Byte dayOfWeek, String slotCode, String scheduleType, 
                               String location, String note) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.slotCode = slotCode;
        this.scheduleType = scheduleType;
        this.location = location;
        this.note = note;
    }

}

