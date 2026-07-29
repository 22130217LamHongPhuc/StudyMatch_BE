package com.example.microservice.controller;

import com.example.microservice.dto.CreateMatchingItemRequest;
import com.example.microservice.dto.DecidedMatchingItemsDto;
import com.example.microservice.dto.MatchingItemResponse;
import com.example.microservice.dto.UpdateMatchingItemStatusRequest;
import com.example.microservice.dto.admin.matching.ApiResponse;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.service.MatchingItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matching-items")
@RequiredArgsConstructor
public class MatchingItemController {

        private final MatchingItemService matchingItemService;

        @PostMapping("/action/view")
        public ResponseEntity<ApiResponse<MatchingItemResponse>> recordAction(
                        @Valid @RequestBody CreateMatchingItemRequest request) {

                MatchingItemResponse response = matchingItemService.recordAction(request);
                return ResponseEntity.ok(new ApiResponse<>(
                                true,
                                StatusCode.SUCCESS,
                                "Record matching action successfully",
                                response));
        }

        @PostMapping("/action/skip")
        public ResponseEntity<ApiResponse<MatchingItemResponse>> recordSkippedAction(
                        @Valid @RequestBody CreateMatchingItemRequest request) {
                MatchingItemResponse response = matchingItemService.recordActionSkipped(request);
                return ResponseEntity.ok(new ApiResponse<>(
                                true,
                                StatusCode.SUCCESS,
                                "Record matching skipped action successfully",
                                response));
        }

        @PostMapping("/action/friend-request")
        public ResponseEntity<ApiResponse<MatchingItemResponse>> recordFriendRequest(
                        @Valid @RequestBody CreateMatchingItemRequest request) {
                MatchingItemResponse response = matchingItemService.recordFriendRequest(request);
                return ResponseEntity.ok(new ApiResponse<>(
                                true,
                                StatusCode.SUCCESS,
                                "Record matching friend request action successfully",
                                response));
        }

        @PostMapping("/action/cancel")
        public ResponseEntity<ApiResponse<MatchingItemResponse>> recordActionCancelled(
                        @Valid @RequestBody CreateMatchingItemRequest request) {
                MatchingItemResponse response = matchingItemService.recordActionCancelled(request);
                return ResponseEntity.ok(new ApiResponse<>(
                                true,
                                StatusCode.SUCCESS,
                                "Record matching cancelled action successfully",
                                response));
        }


        @PatchMapping("/status")
        public ResponseEntity<ApiResponse<MatchingItemResponse>> updateMatchingItemStatus(
                        @Valid @RequestBody UpdateMatchingItemStatusRequest request) {
                MatchingItemResponse response = matchingItemService.updateMatchingItemStatus(request);
                return ResponseEntity.ok(new ApiResponse<>(
                                true,
                                StatusCode.SUCCESS,
                                "Update matching item status successfully",
                                response));
        }

        @GetMapping("/decided/{userId}")
        public ResponseEntity<ApiResponse<DecidedMatchingItemsDto>> getDecidedMatchingItems(
                        @PathVariable Long userId,
                        @RequestParam(defaultValue = "0") Integer page,
                        @RequestParam(defaultValue = "100") Integer size) {
                DecidedMatchingItemsDto response = matchingItemService.getDecidedMatchingItems(userId, page, size);

                return ResponseEntity.ok(new ApiResponse<>(
                                true,
                                StatusCode.SUCCESS,
                                "Get decided matching items successfully",
                                response));
        }

        @GetMapping("/preferences/{userId}")
        public ResponseEntity<ApiResponse<java.util.Map<String, java.util.List<Long>>>> getUserFeedbackPreferences(
                        @PathVariable Long userId) {
                java.util.Map<String, java.util.List<Long>> response = matchingItemService.getUserFeedbackPreferences(userId);
                return ResponseEntity.ok(new ApiResponse<>(
                                true,
                                StatusCode.SUCCESS,
                                "Get user feedback preferences successfully",
                                response));
        }
}