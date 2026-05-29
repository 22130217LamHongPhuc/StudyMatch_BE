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
                response.getCallType()
        );
        messagingTemplate.convertAndSendToUser(
                String.valueOf(response.getTargetUserId()),
                "/queue/chat",
                new SocketEnvelope<>(VIDEO_CALL_INVITE, invite)
        );
        log.info("[VideoCall][BE][start][invite-sent] targetUserId={}, sessionId={}, conversationId={}",
                response.getTargetUserId(),
                response.getSessionId(),
                response.getConversationId());

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
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(response.getTargetUserId()),
                    "/queue/chat",
                    new SocketEnvelope<>(VIDEO_CALL_ACCEPTED, callerResponse)
            );
            log.info("[VideoCall][BE][join][accepted-sent] callerId={}, sessionId={}, roomId={}",
                    response.getTargetUserId(),
                    callerResponse.getSessionId(),
                    callerResponse.getRoomId());
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
        VideoCallSession session = videoCallService.findActiveSession(sessionId);
        Long conversationId = session.getConversation().getId();
        Long targetUserId = chatService.findUserOther(conversationId, userId).orElse(null);
        log.info("[VideoCall][BE][reject][session-found] sessionId={}, conversationId={}, userId={}, targetUserId={}",
                sessionId,
                conversationId,
                userId,
                targetUserId);

        videoCallService.endCall(sessionId, userId);
        MessDTO historyMessage = videoCallService.saveCallHistory(sessionId, userId, "MISSED");
        sendCallHistory(conversationId, userId, targetUserId, historyMessage);
        if (targetUserId != null) {
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(targetUserId),
                    "/queue/chat",
                    new SocketEnvelope<>(VIDEO_CALL_REJECTED, new VideoCallInviteData(
                            sessionId,
                            conversationId,
                            videoCallService.buildRoomId(sessionId),
                            userId,
                            null,
                            null,
                            null
                    ))
            );
            log.info("[VideoCall][BE][reject][socket-sent] targetUserId={}, sessionId={}", targetUserId, sessionId);
        }
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
        log.info("[VideoCall][BE][end][session-found] sessionId={}, conversationId={}, targetUserId={}",
                sessionId,
                conversationId,
                targetUserId);

        videoCallService.endCall(sessionId, userId);
        MessDTO historyMessage = videoCallService.saveCallHistory(sessionId, userId, "COMPLETED");
        sendCallHistory(conversationId, userId, targetUserId, historyMessage);
        log.info("[VideoCall][BE][end][service-success] sessionId={}, userId={}", sessionId, userId);
        if (targetUserId != null) {
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(targetUserId),
                    "/queue/chat",
                    new SocketEnvelope<>(VIDEO_CALL_ENDED, new VideoCallInviteData(
                            sessionId,
                            conversationId,
                            videoCallService.buildRoomId(sessionId),
                            userId,
                            null,
                            null,
                            null
                    ))
            );
            log.info("[VideoCall][BE][end][socket-sent] targetUserId={}, sessionId={}", targetUserId, sessionId);
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
        }
        log.info("[VideoCall][BE][history][socket-sent] conversationId={}, messageId={}, currentUserId={}, targetUserId={}",
                conversationId,
                historyMessage.getMessageId(),
                currentUserId,
                targetUserId);
    }
}
