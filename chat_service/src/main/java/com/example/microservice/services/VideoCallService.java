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
import com.example.microservice.exception.CallUnavailableException;
import com.example.microservice.socket.ActiveCall;
import com.example.microservice.socket.WebSocketSessionManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
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
    private final WebSocketSessionManager sessionManager;

    public record CallResult(ActiveCall call, MessDTO historyMessage) {}

    public ActiveCall findRuntimeCall(Long sessionId, Long userId) {
        return sessionManager.getActiveCall(userId, sessionId);
    }

    @Transactional
    public VideoCallResponse startCall(Long conversationId, Long callerId, String callType) {
        log.info("[VideoCall][BE][service][start] conversationId={}, callerId={}", conversationId, callerId);
        if (conversationId == null) {
            throw new IllegalArgumentException("conversationId is required");
        }
        ensureParticipant(conversationId, callerId);

        Long targetUserId = chatService.findUserOther(conversationId, callerId).orElse(null);
        if (targetUserId != null) {
            ActiveCall activeCall = new ActiveCall(
                    newId(), conversationId, callerId, targetUserId, normalizeCallType(callType));
            WebSocketSessionManager.CallReservation reservation = sessionManager.reserveCall(activeCall);
            if (reservation != WebSocketSessionManager.CallReservation.RESERVED) {
                if (reservation == WebSocketSessionManager.CallReservation.TARGET_BUSY) {
                    saveRuntimeCallHistory(activeCall, callerId, "BUSY");
                    throw new CallUnavailableException("USER_BUSY", "User is busy");
                }
                if (reservation == WebSocketSessionManager.CallReservation.CALLER_BUSY) {
                    throw new CallUnavailableException("CALLER_BUSY", "Caller is already in another call");
                }
                throw new CallUnavailableException("USER_UNREACHABLE", "User is not online");
            }
            return buildRuntimeResponse(activeCall, callerId, targetUserId);
        }

        VideoCallSession session = videoCallSessionRepo
                .findActiveByConversationId(conversationId)
                .orElseGet(() -> createSession(conversationId, normalizeCallType(callType)));
        log.info("[VideoCall][BE][service][start][session-ready] sessionId={}, conversationId={}, startedAt={}",
                session.getId(),
                conversationId,
                session.getStartedAt());
        Set<Long> groupCallUsers = sessionManager.getGroupCallUsers(session.getId());
        if (groupCallUsers.isEmpty()) {
            try {
                Set<Long> reservedUsers = sessionManager.reserveGroupCall(
                        session.getId(),
                        callerId,
                        chatService.findConversationParticipants(conversationId)
                );
                if (reservedUsers.stream().noneMatch(userId -> !callerId.equals(userId))) {
                    sessionManager.releaseGroupCall(session.getId());
                    throw new CallUnavailableException(
                            "USER_UNREACHABLE",
                            "No online group member is available"
                    );
                }
            } catch (IllegalStateException exception) {
                throw new CallUnavailableException(exception.getMessage(), exception.getMessage());
            }
        } else {
            if (groupCallUsers.contains(callerId)) {
                throw new CallUnavailableException(
                        "CALLER_BUSY",
                        "Previous group call is still closing"
                );
            }
            if (!sessionManager.joinGroupCall(session.getId(), callerId)) {
                throw new CallUnavailableException("CALLER_BUSY", "Caller is already in another call");
            }
        }
        joinInternal(session, callerId);

        log.info("[VideoCall][BE][service][start][target-found] conversationId={}, callerId={}, targetUserId={}",
                conversationId,
                callerId,
                targetUserId);
        return buildResponse(session, callerId, targetUserId);
    }

    @Transactional
    public VideoCallResponse joinCall(Long sessionId, Long userId) {
        log.info("[VideoCall][BE][service][join] sessionId={}, userId={}", sessionId, userId);
        ActiveCall runtimeCall = sessionManager.getActiveCall(userId, sessionId);
        if (runtimeCall != null) {
            Instant acceptedAt = Instant.now();
            ActiveCall accepted = sessionManager.acceptCall(sessionId, userId, acceptedAt);
            if (accepted == null) {
                throw new IllegalArgumentException("Video call cannot be accepted");
            }
            VideoCallSession acceptedSession = createAcceptedSession(accepted, acceptedAt);
            joinInternal(acceptedSession, accepted.getCallerId(), acceptedAt);
            joinInternal(acceptedSession, accepted.getCalleeId(), acceptedAt);
            return buildResponse(acceptedSession, userId, accepted.getCallerId());
        }

        VideoCallSession session = findActiveSession(sessionId);
        Long conversationId = session.getConversation().getId();
        ensureParticipant(conversationId, userId);
        boolean groupCall = chatService.findUserOther(conversationId, userId).isEmpty();
        if (groupCall && !sessionManager.joinGroupCall(sessionId, userId)) {
            throw new CallUnavailableException("USER_BUSY", "User is busy");
        }
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
        ActiveCall runtimeCall = sessionManager.getActiveCall(userId, sessionId);
        if (runtimeCall != null) {
            Long targetId = runtimeCall.getCallerId().equals(userId)
                    ? runtimeCall.getCalleeId()
                    : runtimeCall.getCallerId();
            return buildRuntimeResponse(runtimeCall, userId, targetId);
        }
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
        videoCallParticipantRepo.findBySessionIdAndUserId(sessionId, userId)
                .ifPresent(participant -> {
                    log.info("[VideoCall][BE][service][end][participant-left] participantId={}, sessionId={}, userId={}",
                            participant.getId(),
                            sessionId,
                            userId);
                    participant.setLeftAt(endedAt);
                    videoCallParticipantRepo.save(participant);
                });
        boolean groupCall = chatService.findUserOther(conversationId, userId).isEmpty();
        long activeParticipants = videoCallParticipantRepo.findBySessionId(sessionId).stream()
                .filter(participant -> participant.getLeftAt() == null)
                .count();
        if (!groupCall || activeParticipants == 0) {
            session.setEndedAt(endedAt);
            Instant durationStartAt = videoCallParticipantRepo.findBySessionId(sessionId).stream()
                    .map(VideoCallParticipant::getJoinedAt)
                    .filter(joinedAt -> joinedAt != null)
                    .max(Comparator.naturalOrder())
                    .orElse(session.getStartedAt());
            long seconds = durationStartAt == null ? 0 : Duration.between(durationStartAt, endedAt).getSeconds();
            session.setDurationSeconds((int) Math.max(0, seconds));
        }
        videoCallSessionRepo.save(session);
        if (groupCall) {
            sessionManager.leaveGroupCall(sessionId, userId);
            if (activeParticipants == 0) {
                sessionManager.releaseGroupCall(sessionId);
            }
        } else {
            sessionManager.releaseCall(sessionId);
        }
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

    @Transactional
    public CallResult finishRingingCall(Long sessionId, Long actorId, String status) {
        ActiveCall call = sessionManager.getActiveCall(actorId, sessionId);
        if (call == null || call.getState() != ActiveCall.State.RINGING) {
            throw new IllegalArgumentException("Ringing call not found");
        }
        if ("REJECTED".equals(status) && !call.getCalleeId().equals(actorId)) {
            throw new IllegalArgumentException("Only callee can reject this call");
        }
        if ("CANCELLED".equals(status) && !call.getCallerId().equals(actorId)) {
            throw new IllegalArgumentException("Only caller can cancel this call");
        }
        ActiveCall released = sessionManager.releaseCall(sessionId, ActiveCall.State.RINGING);
        if (released == null) {
            throw new IllegalArgumentException("Call is no longer ringing");
        }
        return new CallResult(released, saveRuntimeCallHistory(released, actorId, status));
    }

    @Transactional
    public CallResult expireRingingCall(Long sessionId) {
        ActiveCall call = sessionManager.releaseCall(sessionId, ActiveCall.State.RINGING);
        if (call == null) return null;
        return new CallResult(call, saveRuntimeCallHistory(call, call.getCallerId(), "MISSED"));
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

    public boolean hasJoined(Long sessionId, Long userId) {
        return videoCallParticipantRepo.findBySessionIdAndUserId(sessionId, userId)
                .map(participant -> participant.getLeftAt() == null)
                .orElse(false);
    }

    public boolean isCaller(Long sessionId, Long userId) {
        return getCallerId(sessionId)
                .map(callerId -> callerId.equals(userId))
                .orElse(false);
    }

    public java.util.Optional<Long> getCallerId(Long sessionId) {
        return videoCallParticipantRepo.findBySessionId(sessionId).stream()
                .min(Comparator.comparing(VideoCallParticipant::getJoinedAt))
                .map(VideoCallParticipant::getUserId);
    }

    public List<Long> getActiveParticipantIds(Long sessionId) {
        return videoCallParticipantRepo.findBySessionId(sessionId).stream()
                .filter(participant -> participant.getLeftAt() == null)
                .map(VideoCallParticipant::getUserId)
                .toList();
    }

    public List<Long> getInvitedGroupParticipantIds(Long sessionId, Long callerId) {
        return sessionManager.getGroupCallUsers(sessionId).stream()
                .filter(userId -> !userId.equals(callerId))
                .toList();
    }

    public void rejectGroupInvitation(Long sessionId, Long userId) {
        sessionManager.leaveGroupCall(sessionId, userId);
    }

    @Transactional
    public void terminateCall(Long sessionId) {
        VideoCallSession session = videoCallSessionRepo.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Video call session not found"));
        if (session.getEndedAt() == null) {
            Instant endedAt = Instant.now();
            session.setEndedAt(endedAt);
            Instant startedAt = session.getStartedAt();
            long seconds = startedAt == null ? 0 : Duration.between(startedAt, endedAt).getSeconds();
            session.setDurationSeconds((int) Math.max(0, seconds));
        }
        videoCallSessionRepo.save(session);
        sessionManager.releaseGroupCall(sessionId);
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

    private VideoCallSession createAcceptedSession(ActiveCall call, Instant acceptedAt) {
        Conversation conversation = conversationService.findById(call.getConversationId());
        if (conversation == null) {
            sessionManager.releaseCall(call.getSessionId());
            throw new IllegalArgumentException("Conversation not found");
        }
        VideoCallSession session = new VideoCallSession();
        session.setId(call.getSessionId());
        session.setConversation(conversation);
        session.setStartedAt(acceptedAt);
        session.setRecordingUrl(call.getCallType());
        return videoCallSessionRepo.save(session);
    }

    private void joinInternal(VideoCallSession session, Long userId) {
        joinInternal(session, userId, Instant.now());
    }

    private void joinInternal(VideoCallSession session, Long userId, Instant joinedAt) {
        videoCallParticipantRepo.findBySessionIdAndUserId(session.getId(), userId)
                .ifPresentOrElse(
                        participant -> {
                            if (participant.getLeftAt() != null) {
                                participant.setJoinedAt(joinedAt);
                                participant.setLeftAt(null);
                                videoCallParticipantRepo.save(participant);
                            }
                            log.info("[VideoCall][BE][service][join-internal][exists] participantId={}, sessionId={}, userId={}",
                                    participant.getId(),
                                    session.getId(),
                                    userId);
                        },
                        () -> {
                    VideoCallParticipant participant = new VideoCallParticipant();
                    participant.setId(newId());
                    participant.setSession(session);
                    participant.setUserId(userId);
                    participant.setJoinedAt(joinedAt);
                    VideoCallParticipant saved = videoCallParticipantRepo.save(participant);
                    log.info("[VideoCall][BE][service][join-internal][saved] participantId={}, sessionId={}, userId={}",
                            saved.getId(),
                            session.getId(),
                            userId);
                }
                );
    }

    private void ensureParticipant(Long conversationId, Long userId) {
        boolean exists = chatService.isParticipant(conversationId, userId);
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

    private VideoCallResponse buildRuntimeResponse(ActiveCall call, Long userId, Long targetUserId) {
        String roomId = buildRoomId(call.getSessionId());
        String token = zegoTokenService.generateVideoToken(String.valueOf(userId), roomId);
        return new VideoCallResponse(
                call.getSessionId(),
                call.getConversationId(),
                zegoTokenService.getAppId(),
                roomId,
                userId,
                "user_" + userId,
                token,
                zegoTokenService.getExpiredAt(),
                targetUserId,
                call.getCallType()
        );
    }

    private MessDTO saveRuntimeCallHistory(ActiveCall call, Long actorId, String status) {
        String messageType = "CALL_" + call.getCallType();
        String marker = "\"sessionId\":" + call.getSessionId();
        if (messageRepo.findFirstByConversationIdAndTypeAndContentContaining(
                call.getConversationId(), messageType, marker).isPresent()) {
            return null;
        }

        Message message = new Message();
        message.setConversation(conversationService.findById(call.getConversationId()));
        message.setSenderId(actorId);
        message.setType(messageType);
        message.setContent("{\"sessionId\":" + call.getSessionId()
                + ",\"status\":\"" + status
                + "\",\"callType\":\"" + call.getCallType()
                + "\",\"durationSeconds\":0}");
        message.setCreatedAt(java.time.LocalDateTime.now());
        return new MessDTO(messageRepo.save(message));
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
