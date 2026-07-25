package com.example.microservice.services.controller;

import com.example.microservice.services.Dto.AdminDocumentResponse;
import com.example.microservice.services.Dto.DocumentRejectRequest;
import com.example.microservice.services.Dto.DocumentHideRequest;
import com.example.microservice.services.Dto.PageResponse;
import com.example.microservice.services.config.APIResponse;
import com.example.microservice.services.config.ResponseStatus;
import com.example.microservice.services.entity.DocumentCategory;
import com.example.microservice.services.entity.DocumentStatus;
import com.example.microservice.services.exception.AppException;
import com.example.microservice.services.exception.ErrorCode;
import com.example.microservice.services.service.LearningDocumentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/documents")
@CrossOrigin(origins = "*")
public class AdminLearningDocumentController {

    @Autowired
    private LearningDocumentService learningDocumentService;

    @GetMapping
    public ResponseEntity<APIResponse<PageResponse<AdminDocumentResponse>>> getDocuments(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) DocumentStatus status,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) DocumentCategory category,
            @RequestParam(required = false) Long uploaderId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "newest") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "X-User-Id", required = false) Long adminId) {

        if (adminId == null) {
            throw new AppException(ErrorCode.DOCUMENT_ACCESS_DENIED, "User is not authenticated");
        }

        PageResponse<AdminDocumentResponse> response = learningDocumentService.getDocumentsForAdmin(
                search, status, subjectId, category, uploaderId, startDate, endDate, sortBy, page, size
        );
        APIResponse<PageResponse<AdminDocumentResponse>> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, response);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<APIResponse<AdminDocumentResponse>> getDocumentDetails(
            @PathVariable Long documentId,
            @RequestHeader(value = "X-User-Id", required = false) Long adminId) {

        if (adminId == null) {
            throw new AppException(ErrorCode.DOCUMENT_ACCESS_DENIED, "User is not authenticated");
        }

        AdminDocumentResponse response = learningDocumentService.getAdminDocumentDetails(documentId);
        APIResponse<AdminDocumentResponse> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, response);
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{documentId}/approve")
    public ResponseEntity<APIResponse<AdminDocumentResponse>> approveDocument(
            @PathVariable Long documentId,
            @RequestHeader(value = "X-User-Id", required = false) Long adminId) {

        if (adminId == null) {
            throw new AppException(ErrorCode.DOCUMENT_ACCESS_DENIED, "User is not authenticated");
        }

        AdminDocumentResponse response = learningDocumentService.approveDocument(documentId, adminId);
        APIResponse<AdminDocumentResponse> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, response);
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{documentId}/reject")
    public ResponseEntity<APIResponse<AdminDocumentResponse>> rejectDocument(
            @PathVariable Long documentId,
            @Valid @RequestBody DocumentRejectRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long adminId) {

        if (adminId == null) {
            throw new AppException(ErrorCode.DOCUMENT_ACCESS_DENIED, "User is not authenticated");
        }

        AdminDocumentResponse response = learningDocumentService.rejectDocument(documentId, adminId, request.getRejectionReason());
        APIResponse<AdminDocumentResponse> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, response);
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{documentId}/hide")
    public ResponseEntity<APIResponse<AdminDocumentResponse>> hideDocument(
            @PathVariable Long documentId,
            @Valid @RequestBody DocumentHideRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long adminId) {

        if (adminId == null) {
            throw new AppException(ErrorCode.DOCUMENT_ACCESS_DENIED, "User is not authenticated");
        }

        AdminDocumentResponse response = learningDocumentService.hideDocument(documentId, adminId, request.getHiddenReason());
        APIResponse<AdminDocumentResponse> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, response);
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/{documentId}/restore")
    public ResponseEntity<APIResponse<AdminDocumentResponse>> restoreDocument(
            @PathVariable Long documentId,
            @RequestHeader(value = "X-User-Id", required = false) Long adminId) {

        if (adminId == null) {
            throw new AppException(ErrorCode.DOCUMENT_ACCESS_DENIED, "User is not authenticated");
        }

        AdminDocumentResponse response = learningDocumentService.restoreDocument(documentId, adminId);
        APIResponse<AdminDocumentResponse> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, response);
        return ResponseEntity.ok(apiResponse);
    }
}
