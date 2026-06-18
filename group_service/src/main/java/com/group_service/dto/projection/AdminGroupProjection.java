package com.group_service.dto.projection;

import com.group_service.entity.enums.GroupStatus;
import com.group_service.entity.enums.GroupType;
import com.group_service.entity.enums.GroupVisibility;

import java.time.LocalDateTime;

public interface AdminGroupProjection {

    Long getId();

    String getName();

    GroupType getGroupType();

    GroupVisibility getVisibility();

    String getSubjectName();

    Long getTermId();

    Long getCreatedByUserId();

    Long getOwnerUserId();

    Integer getMaxMembers();

    GroupStatus getStatus();

    LocalDateTime getCreatedAt();

    Long getMemberCount();
}