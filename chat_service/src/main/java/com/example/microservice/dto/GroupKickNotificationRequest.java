package com.example.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupKickNotificationRequest {
    private Long userId;
    private Long groupId;
    private String groupName;
    private String status;
    private String reason;
}
