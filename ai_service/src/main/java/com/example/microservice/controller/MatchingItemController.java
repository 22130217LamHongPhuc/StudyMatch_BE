package com.example.microservice.controller;

import com.example.microservice.dto.CreateMatchingItemRequest;
import com.example.microservice.dto.MatchingItemResponse;
import com.example.microservice.dto.UpdateMatchingItemStatusRequest;
import com.example.microservice.dto.admin.matching.ApiResponse;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.service.MatchingItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/matching-items")
@RequiredArgsConstructor
public class MatchingItemController {

    private final MatchingItemService matchingItemService;

    @PostMapping("/action")
    public ResponseEntity<ApiResponse<MatchingItemResponse>> recordAction(
            @Valid @RequestBody CreateMatchingItemRequest request
    ) {
        MatchingItemResponse response = matchingItemService.recordAction(request);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Record matching action successfully",
                response
        ));
    }

    @PatchMapping("/status")
    public ResponseEntity<ApiResponse<MatchingItemResponse>> updateMatchingItemStatus(
            @Valid @RequestBody UpdateMatchingItemStatusRequest request
    ) {
        MatchingItemResponse response = matchingItemService.updateMatchingItemStatus(request);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                StatusCode.SUCCESS,
                "Update matching item status successfully",
                response
        ));
    }
}