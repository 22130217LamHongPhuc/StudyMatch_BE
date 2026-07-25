package com.group_service.controller;

import com.group_service.dto.StudyDurationTimelineDto;
import com.group_service.dto.SubjectGroupStatDto;
import com.group_service.repository.StudyGroupRepository;
import com.group_service.repository.StudySessionAttendanceLogRepository;
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

    private final StudyGroupRepository studyGroupRepository;
    private final StudySessionAttendanceLogRepository attendanceLogRepository;

    @GetMapping("/subjects")
    public List<SubjectGroupStatDto> getTopSubjects() {
        return studyGroupRepository.findAdminTopSubjects().stream()
                .map(row -> new SubjectGroupStatDto(
                        String.valueOf(row[0]),
                        ((Number) row[1]).longValue(),
                        ((Number) row[2]).longValue(),
                        ((Number) row[3]).longValue(),
                        ((Number) row[4]).longValue()))
                .toList();
    }

    @GetMapping("/study-duration")
    public List<StudyDurationTimelineDto> getStudyDurationTimeline(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return attendanceLogRepository.findAdminStudyDurationTimeline(start, end).stream()
                .map(row -> new StudyDurationTimelineDto(
                        ((Date) row[0]).toLocalDate().format(LABEL_FORMAT),
                        ((Number) row[1]).doubleValue(),
                        ((Number) row[2]).doubleValue(),
                        ((Number) row[3]).doubleValue()))
                .toList();
    }
}
