package com.example.microservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminGroupDetailResponse {
    private Long id;
    private String name;
    private String description;
    private String groupType;
    private String subjectName;
    private String status;
    private Long memberCount;
}
