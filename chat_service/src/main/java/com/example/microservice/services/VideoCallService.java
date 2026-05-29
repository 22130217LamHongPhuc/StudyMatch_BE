package com.example.microservice.services;

import com.example.microservice.dto.VideoCallResponse;
import com.example.microservice.entity.Conversation;
import com.example.microservice.entity.Message;
import com.example.microservice.entity.VideoCallParticipant;
import com.example.microservice.entity.VideoCallSession;
import com.example.microservice.dto.MessDTO;
import com.example.microservice.repository.MessageRepo;
import com.example.microservice.repository.VideoCallParticipantRepo;
import com.example.microservice.repository.VideoCallSessionRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoCallService {
    private final ChatService chatService;
    private final ConversationService conversationService;
    private final VideoCallSessionRepo videoCallSessionRepo;
    private final VideoCallParticipantRepo videoCallParticipantRepo;
    private final ZegoTokenService zegoTokenService;
    private final MessageRepo messageRepo;

    @Transactional
    public VideoCallResponse startCall(Long conversationId, Long callerId, String callType) {
        log.info("[VideoCall][BE][service][start] conversationId={}, callerId={}", conversationId, callerId);
        if (conversationId == null) {
            throw new IllegalArgumentException("conversationId is required");
        }
        ensureParticipant(conversationId, callerId);

        VideoCallSession session = videoCallSessionRepo
                .findActiveByConversationId(conversationId)
                .orElseGet(() -> createSession(conversationId, normalizeCallType(callType)));
        log.info("[VideoCall][BE][service][start][session-ready] sessionId={}, conversationId={}, startedAt={}",
                session.getId(),
                conversationId,
                session.getStartedAt());
        joinInternal(session, callerId);

        Long targetUserId = chatService.findUserOther(conversationId, callerId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find target user in conversation"));
        log.info("[VideoCall][BE][service][start][target-found] conversationId={}, callerId={}, targetUserId={}",
                conversationId,
                callerId,
                targetUserId);
        return buildResponse(session, callerId, targetUserId);
    }

    @Transactional
    public VideoCallResponse joinCall(Long sessionId, Long userId) {
        log.info("[VideoCall][BE][service][join] sessionId={}, userId={}", sessionId, userId);
        VideoCallSession session = findActiveSession(sessionId);
        Long conversationId = session.getConversation().getId();
        ensureParticipant(conversationId, userId);
        joinInternal(session, userId);

        Long targetUserId = chatService.findUserOther(conversationId, userId).orElse(null);
        log.info("[VideoCall][BE][service][join][target-found] sessionId={}, conversationId={}, userId={}, targetUserId={}",
                sessionId,
                conversationId,
                userId,
                targetUserId);
        return buildResponse(session, userId, targetUserId);
    }

    public VideoCallResponse getCallInfo(Long sessionId, Long userId) {
        log.info("[VideoCall][BE][service][get-call-info] sessionId={}, userId={}", sessionId, userId);
        VideoCallSession session = findActiveSession(sessionId);
        Long conversationId = session.getConversation().getId();
        ensureParticipant(conversationId, userId);
        Long targetUserId = chatService.findUserOther(conversationId, userId).orElse(null);
        return buildResponse(session, userId, targetUserId);
    }

    @Transactional
    public void endCall(Long sessionId, Long userId) {
        log.info("[VideoCall][BE][service][end] sessionId={}, userId={}", sessionId, userId);
        VideoCallSession session = findActiveSession(sessionId);
        Long conversationId = session.getConversation().getId();
        ensureParticipant(conversationId, userId);

        Instant endedAt = Instant.now();
        session.setEndedAt(endedAt);
        if (session.getStartedAt() != null) {
            session.setDurationSeconds((int) Duration.between(session.getStartedAt(), endedAt).getSeconds());
        }
        videoCallParticipantRepo.findBySessionIdAndUserId(sessionId, userId)
                .ifPresent(participant -> {
                    log.info("[VideoCall][BE][service][end][participant-left] participantId={}, sessionId={}, userId={}",
                            participant.getId(),
                            sessionId,
                            userId);
                    participant.setLeftAt(endedAt);
                    videoCallParticipantRepo.save(participant);
                });
        videoCallSessionRepo.save(session);
        log.info("[VideoCall][BE][service][end][saved] sessionId={}, durationSeconds={}",
                sessionId,
                session.getDurationSeconds());
    }

    @Transactional
    public MessDTO saveCallHistory(Long sessionId, Long actorId, String status) {
        VideoCallSession session = videoCallSessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Video call session not found"));
        Long conversationId = session.getConversation().getId();
        ensureParticipant(conversationId, actorId);

        String callType = getCallType(session);
        String messageType = "CALL_" + callType;
        String marker = "\"sessionId\":" + sessionId;
        if (messageRepo.findFirstByConversationIdAndTypeAndContentContaining(conversationId, messageType, marker).isPresent()) {
            log.info("[VideoCall][BE][service][history][exists] sessionId={}, type={}", sessionId, messageType);
            return null;
        }

        Message message = new Message();
        message.setConversation(session.getConversation());
        message.setSenderId(actorId);
        message.setType(messageType);
        message.setContent("{\"sessionId\":" + sessionId
                + ",\"status\":\"" + status
                + "\",\"callType\":\"" + callType
                + "\",\"durationSeconds\":" + (session.getDurationSeconds() == null ? 0 : session.getDurationSeconds())
                + "}");
        message.setCreatedAt(java.time.LocalDateTime.now());
        Message saved = messageRepo.save(message);
        log.info("[VideoCall][BE][service][history][saved] messageId={}, sessionId={}, type={}, status={}",
                saved.getId(),
                sessionId,
                messageType,
                status);
        return new MessDTO(saved);
    }

    public VideoCallSession findActiveSession(Long sessionId) {
        log.info("[VideoCall][BE][service][find-active] sessionId={}", sessionId);
        VideoCallSession session = videoCallSessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Video call session not found"));
        if (session.getEndedAt() != null) {
            throw new IllegalArgumentException("Video call session has ended");
        }
        return session;
    }

    public String buildRoomId(Long sessionId) {
        return "studymatch_call_" + sessionId;
    }

    private VideoCallSession createSession(Long conversationId, String callType) {
        log.info("[VideoCall][BE][service][create-session][start] conversationId={}", conversationId);
        Conversation conversation = conversationService.findById(conversationId);
        if (conversation == null) {
            throw new IllegalArgumentException("Conversation not found");
        }

        VideoCallSession session = new VideoCallSession();
        session.setId(newId());
        session.setConversation(conversation);
        session.setStartedAt(Instant.now());
        session.setRecordingUrl(callType);
        VideoCallSession saved = videoCallSessionRepo.save(session);
        log.info("[VideoCall][BE][service][create-session][saved] sessionId={}, conversationId={}",
                saved.getId(),
                conversationId);
        return saved;
    }

    private void joinInternal(VideoCallSession session, Long userId) {
        videoCallParticipantRepo.findBySessionIdAndUserId(session.getId(), userId)
                .ifPresentOrElse(
                        participant -> log.info("[VideoCall][BE][service][join-internal][exists] participantId={}, sessionId={}, userId={}",
                                participant.getId(),
                                session.getId(),
                                userId),
                        () -> {
                    VideoCallParticipant participant = new VideoCallParticipant();
                    participant.setId(newId());
                    participant.setSession(session);
                    participant.setUserId(userId);
                    participant.setJoinedAt(Instant.now());
                    VideoCallParticipant saved = videoCallParticipantRepo.save(participant);
                    log.info("[VideoCall][BE][service][join-internal][saved] participantId={}, sessionId={}, userId={}",
                            saved.getId(),
                            session.getId(),
                            userId);
                }
                );
    }

    private void ensureParticipant(Long conversationId, Long userId) {
        boolean exists = chatService.checkPrivateExist(conversationId, userId);
        log.info("[VideoCall][BE][service][ensure-participant] conversationId={}, userId={}, exists={}",
                conversationId,
                userId,
                exists);
        if (!exists) {
            throw new IllegalArgumentException("User is not a participant of this conversation");
        }
    }

    private VideoCallResponse buildResponse(VideoCallSession session, Long userId, Long targetUserId) {
        String roomId = buildRoomId(session.getId());
        log.info("[VideoCall][BE][service][build-response][before-token] sessionId={}, roomId={}, userId={}, targetUserId={}",
                session.getId(),
                roomId,
                userId,
                targetUserId);
        String token = zegoTokenService.generateVideoToken(String.valueOf(userId), roomId);
        long expiredAt = zegoTokenService.getExpiredAt();
        log.info("[VideoCall][BE][service][build-response][after-token] sessionId={}, tokenLength={}, expiredAt={}",
                session.getId(),
                token == null ? 0 : token.length(),
                expiredAt);
        return new VideoCallResponse(
                session.getId(),
                session.getConversation().getId(),
                zegoTokenService.getAppId(),
                roomId,
                userId,
                "user_" + userId,
                token,
                expiredAt,
                targetUserId,
                getCallType(session)
        );
    }

    private Long newId() {
        return Instant.now().toEpochMilli() * 1000 + ThreadLocalRandom.current().nextInt(1000);
    }

    private String normalizeCallType(String callType) {
        return "VIDEO".equalsIgnoreCase(callType) ? "VIDEO" : "AUDIO";
    }

    private String getCallType(VideoCallSession session) {
        return normalizeCallType(session.getRecordingUrl());
    }
}
