package com.example.microservice.controller;

import com.example.microservice.dto.MessagesTimelineDto;
import com.example.microservice.repository.MessageRepo;
import com.example.microservice.socket.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/admin/overview")
@RequiredArgsConstructor
public class AdminOverviewController {

    private static final DateTimeFormatter LABEL_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final MessageRepo messageRepo;
    private final WebSocketSessionManager sessionManager;

    @GetMapping("/online-count")
    public long getOnlineUsersCount() {
        return sessionManager.getOnlineUsersCount();
    }

    @GetMapping("/messages")
    public List<MessagesTimelineDto> getMessagesTimeline(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return messageRepo.findAdminMessagesTimeline(start, end).stream()
                .map(this::toMessagesTimeline)
                .toList();
    }

    private MessagesTimelineDto toMessagesTimeline(Object[] row) {
        String label = ((Date) row[0]).toLocalDate().format(LABEL_FORMAT);
        long groupMessages = ((Number) row[1]).longValue();
        long privateMessages = ((Number) row[2]).longValue();
        long total = ((Number) row[3]).longValue();
        return new MessagesTimelineDto(label, groupMessages, privateMessages, total);
    }
}
