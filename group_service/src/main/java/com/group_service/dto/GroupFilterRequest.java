package com.group_service.dto;

import com.group_service.entity.enums.GroupStatus;
import com.group_service.entity.enums.GroupType;
import com.group_service.entity.enums.GroupVisibility;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GroupFilterRequest {
    private GroupType type;
    private GroupVisibility visibility;
    private GroupStatus status;
}
