package com.example.microservice.controller;

import com.example.microservice.config.APIResponse;
import com.example.microservice.config.EnumEvent;
import com.example.microservice.dto.MessDTO;
import com.example.microservice.dto.NewMessageData;
import com.example.microservice.dto.SocketEnvelope;
import com.example.microservice.dto.StartVideoCallRequest;
import com.example.microservice.dto.TokenValidateResponse;
import com.example.microservice.dto.VideoCallInviteData;
import com.example.microservice.dto.VideoCallResponse;
import com.example.microservice.entity.VideoCallSession;
import com.example.microservice.feignClient.UserClient;
import com.example.microservice.handle.ResponseStatus;
import com.example.microservice.services.ChatService;
import com.example.microservice.services.VideoCallService;
import com.example.microservice.socket.ActiveCall;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/video-calls")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class VideoCallController {
    private static final String VIDEO_CALL_INVITE = "VIDEO_CALL_INVITE";
    private static final String VIDEO_CALL_ACCEPTED = "VIDEO_CALL_ACCEPTED";
    private static final String VIDEO_CALL_REJECTED = "VIDEO_CALL_REJECTED";
    private static final String VIDEO_CALL_ENDED = "VIDEO_CALL_ENDED";
    private final VideoCallService videoCallService;
    private final ChatService chatService;
    private final UserClient userClient;
    private final SimpMessagingTemplate messagingTemplate;
    private final ScheduledExecutorService callTimeoutScheduler =
            Executors.newSingleThreadScheduledExecutor(runnable -> {
                Thread thread = new Thread(runnable, "video-call-timeout");
                thread.setDaemon(true);
                return thread;
            });

    @PostMapping("/start")
    public ResponseEntity<APIResponse<VideoCallResponse>> startCall(
            @RequestBody StartVideoCallRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        log.info("[VideoCall][BE][start][request] conversationId={}, authPresent={}",
                request == null ? null : request.getConversationId(),
                authorization != null && !authorization.isBlank());
        Long callerId = currentUserId(authorization);
        log.info("[VideoCall][BE][start][validated] callerId={}, conversationId={}",
                callerId,
                request.getConversationId());
        VideoCallResponse response = videoCallService.startCall(request.getConversationId(), callerId, request.getCallType());
        log.info("[VideoCall][BE][start][service-success] sessionId={}, roomId={}, targetUserId={}, hasToken={}",
                response.getSessionId(),
                response.getRoomId(),
                response.getTargetUserId(),
                response.getToken() != null && !response.getToken().isBlank());

        VideoCallInviteData invite = new VideoCallInviteData(
                response.getSessionId(),
                response.getConversationId(),
                response.getRoomId(),
                callerId,
                request.getCallerName(),
                request.getCallerAvatar(),
                response.getCallType(),
                response.getTargetUserId() == null
        );
        if (response.getTargetUserId() != null) {
            sendToUser(response.getTargetUserId(), new SocketEnvelope<>(VIDEO_CALL_INVITE, invite));
        } else {
            videoCallService.getInvitedGroupParticipantIds(response.getSessionId(), callerId).stream()
                    .forEach(id -> sendToUser(id, new SocketEnvelope<>(VIDEO_CALL_INVITE, invite)));
        }
        log.info("[VideoCall][BE][start][invite-sent] targetUserId={}, sessionId={}, conversationId={}",
                response.getTargetUserId(),
                response.getSessionId(),
                response.getConversationId());
        if (response.getTargetUserId() != null) {
            callTimeoutScheduler.schedule(
                    () -> expireMissedCall(response.getSessionId()),
                    45,
                    TimeUnit.SECONDS
            );
        }

        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, response));
    }

    @PostMapping("/{sessionId}/join")
    public ResponseEntity<APIResponse<VideoCallResponse>> joinCall(
            @PathVariable Long sessionId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        log.info("[VideoCall][BE][join][request] sessionId={}, authPresent={}",
                sessionId,
                authorization != null && !authorization.isBlank());
        Long userId = currentUserId(authorization);
        log.info("[VideoCall][BE][join][validated] sessionId={}, userId={}", sessionId, userId);
        VideoCallResponse response = videoCallService.joinCall(sessionId, userId);
        log.info("[VideoCall][BE][join][success] sessionId={}, roomId={}, userId={}, hasToken={}",
                response.getSessionId(),
                response.getRoomId(),
                response.getUserId(),
                response.getToken() != null && !response.getToken().isBlank());
        if (response.getTargetUserId() != null) {
            VideoCallResponse callerResponse = videoCallService.getCallInfo(sessionId, response.getTargetUserId());
            sendToUser(response.getTargetUserId(), new SocketEnvelope<>(VIDEO_CALL_ACCEPTED, callerResponse));
        } else {
            videoCallService.getCallerId(sessionId)
                    .filter(callerId -> !userId.equals(callerId))
                    .ifPresent(callerId -> {
                        VideoCallResponse callerResponse = videoCallService.getCallInfo(sessionId, callerId);
                        sendToUser(callerId, new SocketEnvelope<>(VIDEO_CALL_ACCEPTED, callerResponse));
                    });
        }
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, response));
    }

    @PostMapping("/{sessionId}/reject")
    public ResponseEntity<APIResponse<Void>> rejectCall(
            @PathVariable Long sessionId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        log.info("[VideoCall][BE][reject][request] sessionId={}, authPresent={}",
                sessionId,
                authorization != null && !authorization.isBlank());
        Long userId = currentUserId(authorization);
        ActiveCall runtimeCall = videoCallService.findRuntimeCall(sessionId, userId);
        if (runtimeCall != null) {
            VideoCallService.CallResult result =
                    videoCallService.finishRingingCall(sessionId, userId, "REJECTED");
            sendCallHistory(
                    result.call().getConversationId(),
                    userId,
                    result.call().getCallerId(),
                    result.historyMessage()
            );
            sendToUser(result.call().getCallerId(), new SocketEnvelope<>(
                    VIDEO_CALL_REJECTED,
                    toInvite(result.call(), userId)
            ));
            return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, null));
        }
        VideoCallSession session = videoCallService.findActiveSession(sessionId);
        Long conversationId = session.getConversation().getId();
        Long targetUserId = chatService.findUserOther(conversationId, userId).orElse(null);
        boolean groupCallerCancelling = targetUserId == null && videoCallService.isCaller(sessionId, userId);
        log.info("[VideoCall][BE][reject][session-found] sessionId={}, conversationId={}, userId={}, targetUserId={}",
                sessionId,
                conversationId,
                userId,
                targetUserId);

        videoCallService.endCall(sessionId, userId);
        if (targetUserId != null || groupCallerCancelling) {
            MessDTO historyMessage = videoCallService.saveCallHistory(sessionId, userId, "MISSED");
            sendCallHistory(conversationId, userId, targetUserId, historyMessage);
        }
        var rejected = new SocketEnvelope<>(VIDEO_CALL_REJECTED, new VideoCallInviteData(
                sessionId, conversationId, videoCallService.buildRoomId(sessionId), userId, null, null, null,
                targetUserId == null));
        if (targetUserId != null) {
            sendToUser(targetUserId, rejected);
        } else if (groupCallerCancelling) {
            chatService.findConversationParticipants(conversationId).stream()
                    .filter(id -> !userId.equals(id))
                    .forEach(id -> sendToUser(id, rejected));
        }
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, null));
    }

    @PostMapping("/{sessionId}/cancel")
    public ResponseEntity<APIResponse<Void>> cancelCall(
            @PathVariable Long sessionId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        Long userId = currentUserId(authorization);
        VideoCallService.CallResult result =
                videoCallService.finishRingingCall(sessionId, userId, "CANCELLED");
        ActiveCall call = result.call();
        sendCallHistory(call.getConversationId(), userId, call.getCalleeId(), result.historyMessage());
        sendToUser(call.getCalleeId(), new SocketEnvelope<>(
                EnumEvent.VIDEO_CALL_CANCELLED.toString(),
                toInvite(call, userId)
        ));
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, null));
    }

    @PostMapping("/{sessionId}/end")
    public ResponseEntity<APIResponse<Void>> endCall(
            @PathVariable Long sessionId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        log.info("[VideoCall][BE][end][request] sessionId={}, authPresent={}",
                sessionId,
                authorization != null && !authorization.isBlank());
        Long userId = currentUserId(authorization);
        log.info("[VideoCall][BE][end][validated] sessionId={}, userId={}", sessionId, userId);
        VideoCallSession session = videoCallService.findActiveSession(sessionId);
        Long conversationId = session.getConversation().getId();
        Long targetUserId = chatService.findUserOther(conversationId, userId).orElse(null);
        boolean groupCallerEnding = targetUserId == null && videoCallService.isCaller(sessionId, userId);
        log.info("[VideoCall][BE][end][session-found] sessionId={}, conversationId={}, targetUserId={}",
                sessionId,
                conversationId,
                targetUserId);

        videoCallService.endCall(sessionId, userId);
        boolean groupCall = targetUserId == null;
        boolean lastPairDisconnected = groupCall
                && videoCallService.getActiveParticipantIds(sessionId).size() <= 1;
        boolean groupShouldEnd = groupCall && (groupCallerEnding || lastPairDisconnected);
        if (groupShouldEnd) {
            videoCallService.terminateCall(sessionId);
        }
        if (targetUserId != null || groupShouldEnd) {
            MessDTO historyMessage = videoCallService.saveCallHistory(sessionId, userId, "COMPLETED");
            sendCallHistory(conversationId, userId, targetUserId, historyMessage);
        }
        log.info("[VideoCall][BE][end][service-success] sessionId={}, userId={}", sessionId, userId);
        var ended = new SocketEnvelope<>(VIDEO_CALL_ENDED, new VideoCallInviteData(
                sessionId, conversationId, videoCallService.buildRoomId(sessionId), userId, null, null, null,
                targetUserId == null));
        if (targetUserId != null) {
            sendToUser(targetUserId, ended);
        } else if (groupShouldEnd) {
            chatService.findConversationParticipants(conversationId).stream()
                    .filter(id -> !userId.equals(id))
                    .forEach(id -> sendToUser(id, ended));
        }
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, null));
    }

    private Long currentUserId(String authorization) {
        log.info("[VideoCall][BE][auth][validate-start] authPresent={}, bearer={}",
                authorization != null && !authorization.isBlank(),
                authorization != null && authorization.startsWith("Bearer "));
        TokenValidateResponse response = userClient.validateToken(authorization);
        log.info("[VideoCall][BE][auth][validate-response] valid={}, userId={}, message={}",
                response != null && response.isValid(),
                response == null ? null : response.getUserId(),
                response == null ? null : response.getMessage());
        if (response == null || !response.isValid()) {
            throw new IllegalArgumentException("Invalid token");
        }
        return response.getUserId();
    }

    private void sendToUser(Long userId, Object payload) {
        messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/queue/chat", payload);
    }

    private void expireMissedCall(Long sessionId) {
        try {
            VideoCallService.CallResult result = videoCallService.expireRingingCall(sessionId);
            if (result == null) return;
            ActiveCall call = result.call();
            sendCallHistory(
                    call.getConversationId(),
                    call.getCallerId(),
                    call.getCalleeId(),
                    result.historyMessage()
            );
            SocketEnvelope<VideoCallInviteData> ended = new SocketEnvelope<>(
                    EnumEvent.VIDEO_CALL_MISSED.toString(),
                    toInvite(call, call.getCallerId())
            );
            sendToUser(call.getCallerId(), ended);
            sendToUser(call.getCalleeId(), ended);
        } catch (Exception error) {
            log.error("[VideoCall][BE][timeout-error] sessionId={}", sessionId, error);
        }
    }

    private VideoCallInviteData toInvite(ActiveCall call, Long actorId) {
        return new VideoCallInviteData(
                call.getSessionId(),
                call.getConversationId(),
                videoCallService.buildRoomId(call.getSessionId()),
                actorId,
                null,
                null,
                call.getCallType()
        );
    }

    private void sendCallHistory(Long conversationId, Long currentUserId, Long targetUserId, MessDTO historyMessage) {
        if (historyMessage == null) {
            return;
        }
        SocketEnvelope<NewMessageData> envelope = new SocketEnvelope<>(
                EnumEvent.NEW_MESSAGE.toString(),
                new NewMessageData(conversationId, historyMessage)
        );
        messagingTemplate.convertAndSendToUser(String.valueOf(currentUserId), "/queue/chat", envelope);
        if (targetUserId != null) {
            messagingTemplate.convertAndSendToUser(String.valueOf(targetUserId), "/queue/chat", envelope);
        } else {
            chatService.findConversationParticipants(conversationId).stream()
                    .filter(userId -> !userId.equals(currentUserId))
                    .forEach(userId ->
                            messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/queue/chat", envelope)
                    );
        }
        log.info("[VideoCall][BE][history][socket-sent] conversationId={}, messageId={}, currentUserId={}, targetUserId={}",
                conversationId,
                historyMessage.getMessageId(),
                currentUserId,
                targetUserId);
    }
}
