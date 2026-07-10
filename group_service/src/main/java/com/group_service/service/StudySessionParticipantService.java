package com.group_service.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.group_service.clients.UserClient;
import com.group_service.dto.ApiResponse;
import com.group_service.dto.BasicUserResponse;
import com.group_service.dto.CreateStudySessionRequest;
import com.group_service.entity.GroupMember;
import com.group_service.entity.StudySession;
import com.group_service.entity.StudySessionParticipant;
import com.group_service.entity.enums.GroupMemberStatus;
import com.group_service.repository.GroupMemberRepository;
import com.group_service.repository.StudySessionParticipantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mapper.StudySessionParticipantMapper;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudySessionParticipantService {

    private final GroupMemberRepository groupMemberRepository;
    private final StudySessionParticipantMapper studySessionParticipantMapper;
    private final UserClient userClient;
    private final StudySessionParticipantRepository participantRepository;

    public List<StudySessionParticipant> createParticipantsForGroupSession(Long groupId,
            CreateStudySessionRequest request, StudySession saved) {

        List<GroupMember> activeMembers = groupMemberRepository.findByGroupIdAndStatus(groupId,
                GroupMemberStatus.ACTIVE);
        if (activeMembers.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group has no active members");
        }

        List<Long> userIds = activeMembers.stream()
                .map(GroupMember::getUserId)
                .distinct()
                .toList();

        Map<Long, String> userNames = fetchUserNames(userIds);
        List<StudySessionParticipant> participants = activeMembers.stream()
                .map(member -> {
                    Long memberUserId = member.getUserId();
                    boolean isHost = memberUserId.equals(request.getCreatedByUserId());
                    String userName = userNames.getOrDefault(memberUserId, "user_" + memberUserId);

                    return studySessionParticipantMapper.mapToStudySessionParticipant(memberUserId, isHost, userName,
                            saved);
                })
                .toList();

        participantRepository.saveAll(participants);

        return participants;
    }

    private Map<Long, String> fetchUserNames(List<Long> userIds) {
        Map<Long, String> result = new HashMap<>();
        try {
            ApiResponse<List<BasicUserResponse>> response = userClient.getBasicUsers(userIds);
            if (response == null || response.getData() == null) {
                return result;
            }
            for (BasicUserResponse user : response.getData()) {
                if (user == null || user.getUserId() == null) {
                    continue;
                }
                String userName = user.getUserName();
                if (!StringUtils.hasText(userName)) {
                    userName = user.getFullName();
                }
                if (StringUtils.hasText(userName)) {
                    result.put(user.getUserId(), userName);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to fetch basic users for ids={}: {}", userIds, e.getMessage());
        }
        return result;
    }

}
