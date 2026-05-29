package com.group_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionReminderRequest {
    private Long userId;
    private String sessionTitle;
    private String startTime;
    private String groupName;
}
