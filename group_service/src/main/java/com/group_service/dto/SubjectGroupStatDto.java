package com.group_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectGroupStatDto {
    private String subjectName;
    private long publicCount;
    private long privateCount;
    private long totalGroups;
    private long totalMembers;
}
