package com.group_service.dto;

import com.group_service.entity.enums.GroupStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder

public class AdminGroupResponse {

    private Long id;

    private String name;

    private String avatarUrl;

    private String type;

    private String visibility;

    private String subjectName;


    private Long memberCount;

    private GroupStatus status;

    private LocalDateTime createdAt;
}