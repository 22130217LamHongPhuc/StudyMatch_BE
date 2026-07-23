package com.example.microservice.service;

import com.example.microservice.dto.request.AuditLogSaveRequest;
import com.example.microservice.entity.AuditLog;
import com.example.microservice.dto.respone.AuditLogResponse;
import com.example.microservice.dto.respone.AuditLogFiltersResponse;
import com.example.microservice.dto.respone.PageResponse;
import com.example.microservice.repository.AuditLogRepository;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class AuditLogService {

    AuditLogRepository auditLogRepository;

    public AuditLogFiltersResponse getFilters() {
        List<String> actions = auditLogRepository.findDistinctActions();
        List<String> targetTypes = auditLogRepository.findDistinctTargetTypes();
        return new AuditLogFiltersResponse(actions, targetTypes);
    }

    public void saveAuditLog(AuditLogSaveRequest request) {
        AuditLog log = new AuditLog();
        log.setAdminId(request.getAdminId());
        log.setAction(request.getAction());
        log.setTargetId(request.getTargetId());
        log.setTargetType(request.getTargetType());
        log.setDetails(request.getDetails());
        log.setIpAddress(request.getIpAddress());
        auditLogRepository.save(log);
    }

    public PageResponse<AuditLogResponse> getAuditLogs(int page, int size, String keyword, String action, String targetType) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AuditLogResponse> logPage = auditLogRepository.findAuditLogs(keyword, action, targetType, pageable);
        List<AuditLogResponse> items = logPage.getContent();

        return PageResponse.<AuditLogResponse>builder()
                .content(items)
                .page(page)
                .limit(size)
                .totalPages(logPage.getTotalPages())
                .totalElements(logPage.getTotalElements())
                .hasNext(logPage.hasNext())
                .hasPrevious(logPage.hasPrevious())
                .build();
    }
}
