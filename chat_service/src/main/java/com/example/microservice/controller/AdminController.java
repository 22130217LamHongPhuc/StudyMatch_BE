package com.example.microservice.controller;

import com.example.microservice.config.APIResponse;
import com.example.microservice.entity.Message;
import com.example.microservice.repository.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private MessageRepo messageRepo;

    @GetMapping("/messages/violations")
    public ResponseEntity<?> getViolations(
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        Page<Message> result;
        if (status == null || status.isBlank()) {
            result = messageRepo.findByModerationStatusNotOrderByCreatedAtDesc("NONE", PageRequest.of(page, size));
        } else {
            result = messageRepo.findByModerationStatusOrderByCreatedAtDesc(status.toUpperCase(),
                    PageRequest.of(page, size));
        }
        APIResponse<Page<Message>> res = new APIResponse<>(com.example.microservice.handle.ResponseStatus.SUCCESS,
                result);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/messages/top-offenders")
    public ResponseEntity<?> getTopOffenders(@RequestParam(name = "limit", defaultValue = "10") int limit) {
        List<Object[]> rows = messageRepo.findTopOffenders(limit);
        List<Map<String, Object>> out = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> m = new HashMap<>();
            m.put("senderId", row[0]);
            m.put("count", ((Number) row[1]).longValue());
            out.add(m);
        }
        APIResponse<List<Map<String, Object>>> res = new APIResponse<>(
                com.example.microservice.handle.ResponseStatus.SUCCESS, out);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/messages/top-groups")
    public ResponseEntity<?> getTopGroups(@RequestParam(name = "limit", defaultValue = "10") int limit) {
        List<Object[]> rows = messageRepo.findTopGroupsWithViolations(limit);
        List<Map<String, Object>> out = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> m = new HashMap<>();
            m.put("groupId", row[0]);
            m.put("count", ((Number) row[1]).longValue());
            out.add(m);
        }
        APIResponse<List<Map<String, Object>>> res = new APIResponse<>(
                com.example.microservice.handle.ResponseStatus.SUCCESS, out);
        return ResponseEntity.ok(res);
    }
}
