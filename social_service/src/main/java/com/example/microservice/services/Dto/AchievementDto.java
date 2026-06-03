package com.example.microservice.services.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AchievementDto {
    private String code;
    private String title;
    private String description;
    private boolean achieved;
    private Long progress;
    private Long target;
}
