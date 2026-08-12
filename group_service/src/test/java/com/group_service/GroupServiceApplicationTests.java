package com.group_service;

import com.group_service.entity.GroupMember;
import com.group_service.repository.GroupMemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@SpringBootTest
class GroupServiceApplicationTests {

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Test
    void testQueryMember() {
        System.out.println("====== DIAGNOSTIC TEST START ======");
        Optional<GroupMember> memberOpt = groupMemberRepository.findByGroupIdAndUserId(2L, 57L);
        if (memberOpt.isPresent()) {
            GroupMember member = memberOpt.get();
            System.out.println("Found member: ID=" + member.getId() + ", GroupId=" + member.getGroupId() + ", UserId=" + member.getUserId() + ", Status=" + member.getStatus() + ", Role=" + member.getRole());
        } else {
            System.out.println("Member with GroupId=2 and UserId=57 NOT FOUND in database!");
        }

        System.out.println("All members in group 2:");
        List<GroupMember> allInGroup = groupMemberRepository.findByGroupIdAndStatus(2L, com.group_service.entity.enums.GroupMemberStatus.ACTIVE);
        System.out.println("Active members in group 2 count: " + allInGroup.size());
        for (GroupMember gm : allInGroup) {
            System.out.println("Active - ID=" + gm.getId() + ", UserId=" + gm.getUserId() + ", Status=" + gm.getStatus());
        }

        List<GroupMember> allGroupMembers = groupMemberRepository.findAll();
        System.out.println("Total group members in database: " + allGroupMembers.size());
        for (GroupMember gm : allGroupMembers) {
            if (gm.getGroupId() == 2L) {
                System.out.println("Group 2 Member - ID=" + gm.getId() + ", UserId=" + gm.getUserId() + ", Status=" + gm.getStatus());
            }
        }
        System.out.println("====== DIAGNOSTIC TEST END ======");
    }
}
