package com.example.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupKickNotificationRequest {
    private Long userId;
    private Long groupId;
    private String groupName;
}
