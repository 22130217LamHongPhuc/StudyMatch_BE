package com.example.microservice.controller;

import com.example.microservice.dto.admin.matching.MatchingActionResponse;
import com.example.microservice.dto.admin.matching.MatchingStatisticsResponse;
import com.example.microservice.dto.admin.matching.PageResponse;
import com.example.microservice.dto.admin.matching.StudyFeedbackResponse;
import com.example.microservice.dto.admin.matching.StudyFeedbackStatisticsResponse;
import com.example.microservice.enums.MatchingActionStatus;
import com.example.microservice.enums.StudySessionType;
import com.example.microservice.service.AdminMatchingService;
import com.example.microservice.dto.admin.matching.MatchingTrendResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/matching")
@Validated
public class AdminMatchingController {

    private final AdminMatchingService adminMatchingService;

    public AdminMatchingController(AdminMatchingService adminMatchingService) {
        this.adminMatchingService = adminMatchingService;
    }

    @GetMapping("/statistics")
    public MatchingStatisticsResponse getStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return adminMatchingService.getStatistics(fromDate, toDate);
    }

    @GetMapping("/action-distribution")
    public Map<String, Long> getActionDistribution(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return adminMatchingService.getActionDistribution(fromDate, toDate);
    }

    @GetMapping("/trend")
    public List<MatchingTrendResponse> getTrend(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return adminMatchingService.getTrend(fromDate, toDate);
    }

    @GetMapping("/actions")
    public PageResponse<MatchingActionResponse> getActions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long recommendedUserId,
            @RequestParam(required = false) MatchingActionStatus actionStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return adminMatchingService.getActions(
                page,
                size,
                userId,
                recommendedUserId,
                actionStatus,
                fromDate,
                toDate);
    }

    @GetMapping("/feedbacks")
    public PageResponse<StudyFeedbackResponse> getFeedbacks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) StudySessionType sessionType,
            @RequestParam(required = false) Long reviewerUserId,
            @RequestParam(required = false) Long targetUserId,
            @RequestParam(required = false) Long groupId,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return adminMatchingService.getFeedbacks(page, size, sessionType, reviewerUserId, targetUserId, groupId,
                minRating, fromDate, toDate);
    }

    @GetMapping("/feedbacks/{feedbackId}")
    public StudyFeedbackResponse getFeedbackDetail(@PathVariable Long feedbackId) {
        return adminMatchingService.getFeedbackDetail(feedbackId);
    }

    @GetMapping("/feedbacks/statistics")
    public StudyFeedbackStatisticsResponse getFeedbackStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return adminMatchingService.getFeedbackStatistics(fromDate, toDate);
    }
}
