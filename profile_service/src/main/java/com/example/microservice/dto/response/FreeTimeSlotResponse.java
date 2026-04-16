package com.example.microservice.dto.response;

public class FreeTimeSlotResponse {
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

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Byte getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Byte dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getSlotCode() {
        return slotCode;
    }

    public void setSlotCode(String slotCode) {
        this.slotCode = slotCode;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}

