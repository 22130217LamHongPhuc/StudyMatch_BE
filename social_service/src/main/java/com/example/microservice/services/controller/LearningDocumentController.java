package com.example.microservice.services.controller;

import com.example.microservice.services.Dto.CreateLearningDocumentRequest;
import com.example.microservice.services.Dto.DocumentSummaryResponse;
import com.example.microservice.services.Dto.LearningDocumentResponse;
import com.example.microservice.services.Dto.DocumentRatingRequest;
import com.example.microservice.services.Dto.DocumentRatingResponse;
import com.example.microservice.services.Dto.CreateDocumentReportRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class LearningDocumentController {

    @Autowired
    private LearningDocumentService learningDocumentService;

    @PostMapping
    public ResponseEntity<APIResponse<LearningDocumentResponse>> uploadDocument(
            @Valid @RequestBody CreateLearningDocumentRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long uploaderId) {

        if (uploaderId == null) {
            throw new AppException(ErrorCode.DOCUMENT_ACCESS_DENIED, "User is not authenticated");
        }

        LearningDocumentResponse response = learningDocumentService.uploadDocument(request, uploaderId);
        APIResponse<LearningDocumentResponse> apiResponse = new APIResponse<>(ResponseStatus.CREATED, response);
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<APIResponse<PageResponse<DocumentSummaryResponse>>> getDocuments(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) DocumentCategory category,
            @RequestParam(required = false) String fileType,
            @RequestParam(required = false) Double minRating,
            @RequestParam(defaultValue = "newest") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<DocumentSummaryResponse> response = learningDocumentService.getDocuments(
                search, subjectId, category, fileType, minRating, sortBy, page, size
        );
        APIResponse<PageResponse<DocumentSummaryResponse>> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, response);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<APIResponse<LearningDocumentResponse>> getDocumentDetails(@PathVariable Long documentId) {
        LearningDocumentResponse response = learningDocumentService.getDocumentDetails(documentId);
        APIResponse<LearningDocumentResponse> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, response);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/featured")
    public ResponseEntity<APIResponse<List<DocumentSummaryResponse>>> getFeaturedDocuments(
            @RequestParam(defaultValue = "10") int limit) {

        List<DocumentSummaryResponse> response = learningDocumentService.getFeaturedDocuments(limit);
        APIResponse<List<DocumentSummaryResponse>> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, response);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{documentId}/preview")
    public ResponseEntity<Void> previewDocument(@PathVariable Long documentId) {
        String fileUrl = learningDocumentService.getPreviewUrl(documentId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(java.net.URI.create(fileUrl))
                .build();
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<Void> downloadDocument(@PathVariable Long documentId) {
        String fileUrl = learningDocumentService.getDownloadUrl(documentId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(java.net.URI.create(fileUrl))
                .build();
    }

    @PostMapping("/{documentId}/bookmark")
    public ResponseEntity<APIResponse<String>> bookmarkDocument(
            @PathVariable Long documentId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            throw new AppException(ErrorCode.DOCUMENT_ACCESS_DENIED, "User is not authenticated");
        }

        learningDocumentService.bookmarkDocument(documentId, userId);
        APIResponse<String> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, "Lưu tài liệu thành công");
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{documentId}/bookmark")
    public ResponseEntity<APIResponse<String>> removeBookmark(
            @PathVariable Long documentId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            throw new AppException(ErrorCode.DOCUMENT_ACCESS_DENIED, "User is not authenticated");
        }

        learningDocumentService.removeBookmark(documentId, userId);
        APIResponse<String> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, "Hủy lưu tài liệu thành công");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/me/bookmarks")
    public ResponseEntity<APIResponse<PageResponse<DocumentSummaryResponse>>> getMyBookmarks(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (userId == null) {
            throw new AppException(ErrorCode.DOCUMENT_ACCESS_DENIED, "User is not authenticated");
        }

        PageResponse<DocumentSummaryResponse> response = learningDocumentService.getMyBookmarks(userId, page, size);
        APIResponse<PageResponse<DocumentSummaryResponse>> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, response);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/me/documents")
    public ResponseEntity<APIResponse<PageResponse<LearningDocumentResponse>>> getMyUploadedDocuments(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(required = false) DocumentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (userId == null) {
            throw new AppException(ErrorCode.DOCUMENT_ACCESS_DENIED, "User is not authenticated");
        }

        PageResponse<LearningDocumentResponse> response = learningDocumentService.getMyUploadedDocuments(userId, status, page, size);
        APIResponse<PageResponse<LearningDocumentResponse>> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, response);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{documentId}/bookmark-status")
    public ResponseEntity<APIResponse<Boolean>> getBookmarkStatus(
            @PathVariable Long documentId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            throw new AppException(ErrorCode.DOCUMENT_ACCESS_DENIED, "User is not authenticated");
        }

        boolean isBookmarked = learningDocumentService.isBookmarked(documentId, userId);
        APIResponse<Boolean> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, isBookmarked);
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{documentId}/rating")
    public ResponseEntity<APIResponse<DocumentRatingResponse>> rateDocument(
            @PathVariable Long documentId,
            @Valid @RequestBody DocumentRatingRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            throw new AppException(ErrorCode.DOCUMENT_ACCESS_DENIED, "User is not authenticated");
        }

        DocumentRatingResponse response = learningDocumentService.rateDocument(
                documentId, userId, request.getScore(), request.getReview()
        );
        APIResponse<DocumentRatingResponse> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, response);
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{documentId}/rating")
    public ResponseEntity<APIResponse<String>> deleteRating(
            @PathVariable Long documentId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            throw new AppException(ErrorCode.DOCUMENT_ACCESS_DENIED, "User is not authenticated");
        }

        learningDocumentService.deleteRating(documentId, userId);
        APIResponse<String> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, "Xóa đánh giá thành công");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{documentId}/rating/me")
    public ResponseEntity<APIResponse<DocumentRatingResponse>> getMyRating(
            @PathVariable Long documentId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            throw new AppException(ErrorCode.DOCUMENT_ACCESS_DENIED, "User is not authenticated");
        }

        DocumentRatingResponse response = learningDocumentService.getMyRating(documentId, userId);
        APIResponse<DocumentRatingResponse> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, response);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{documentId}/ratings")
    public ResponseEntity<APIResponse<PageResponse<DocumentRatingResponse>>> getDocumentRatings(
            @PathVariable Long documentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<DocumentRatingResponse> response = learningDocumentService.getDocumentRatings(documentId, page, size);
        APIResponse<PageResponse<DocumentRatingResponse>> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, response);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{documentId}/reports")
    public ResponseEntity<APIResponse<String>> reportDocument(
            @PathVariable Long documentId,
            @Valid @RequestBody CreateDocumentReportRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            throw new AppException(ErrorCode.DOCUMENT_ACCESS_DENIED, "User is not authenticated");
        }

        learningDocumentService.reportDocument(documentId, userId, request);
        APIResponse<String> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, "Gửi báo cáo thành công");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{documentId}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable Long documentId) {
        return ResponseEntity.ok(learningDocumentService.existsById(documentId));
    }
}
