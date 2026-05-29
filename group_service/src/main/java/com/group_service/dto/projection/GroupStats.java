package com.group_service.dto.projection;

public interface GroupStats {
        long getTotalGroup();
        long getCommunityGroup();
        long getPrivateGroup();
        long getPublicGroup();
}
