package com.example.microservice.services.service;

import com.example.microservice.services.Dto.CreateLearningDocumentRequest;
import com.example.microservice.services.Dto.DocumentSummaryResponse;
import com.example.microservice.services.Dto.LearningDocumentResponse;
import com.example.microservice.services.Dto.DocumentRatingResponse;
import com.example.microservice.services.Dto.AdminDocumentResponse;
import com.example.microservice.services.Dto.PageResponse;
import com.example.microservice.services.Dto.CreateDocumentReportRequest;
import com.example.microservice.services.Dto.UserServiceClientReportRequest;
import com.example.microservice.services.client.UserServiceClient;
import com.example.microservice.services.entity.DocumentBookmark;
import com.example.microservice.services.entity.DocumentCategory;
import com.example.microservice.services.entity.DocumentStatus;
import com.example.microservice.services.entity.LearningDocument;
import com.example.microservice.services.entity.DocumentRating;
import com.example.microservice.services.exception.AppException;
import com.example.microservice.services.exception.ErrorCode;
import com.example.microservice.services.mapper.LearningDocumentMapper;
import com.example.microservice.services.repository.DocumentBookmarkRepo;
import com.example.microservice.services.repository.DocumentRatingRepo;
import com.example.microservice.services.repository.LearningDocumentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.microservice.services.Dto.BasicUserResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LearningDocumentService {

    @Autowired
    private LearningDocumentRepo learningDocumentRepo;

    @Autowired
    private DocumentBookmarkRepo documentBookmarkRepo;

    @Autowired
    private DocumentRatingRepo documentRatingRepo;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private LearningDocumentMapper mapper;

    @Autowired
    private DocumentFileValidator fileValidator;

    @Autowired
    private ContentModerationService contentModerationService;


    @Transactional
    public LearningDocumentResponse uploadDocument(CreateLearningDocumentRequest request, Long uploaderId) {
        contentModerationService.validateTexts(request.getTitle(), request.getDescription());
        fileValidator.validate(
                request.getFileUrl(),
                request.getStorageKey(),
                request.getFileType(),
                request.getMimeType(),
                request.getFileSize()
        );

        LearningDocument entity = mapper.toEntity(request, uploaderId);
        LearningDocument saved = learningDocumentRepo.save(entity);
        LearningDocumentResponse response = mapper.toResponse(saved);
        response.setUploaderName(getUploaderName(uploaderId));
        return response;
    }

    @Transactional
    public LearningDocumentResponse getDocumentDetails(Long id) {
        LearningDocument document = learningDocumentRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Không tìm thấy tài liệu học liệu"));
        if (document.getStatus() != DocumentStatus.PUBLISHED) {
            throw new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Không tìm thấy tài liệu học liệu");
        }
        document.setViewCount(document.getViewCount() + 1);
        LearningDocument saved = learningDocumentRepo.save(document);
        LearningDocumentResponse response = mapper.toResponse(saved);
        response.setUploaderName(getUploaderName(saved.getUploaderId()));
        return response;
    }

    public List<DocumentSummaryResponse> getFeaturedDocuments(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<LearningDocument> documents = learningDocumentRepo.findFeaturedDocuments(pageable);
        List<Long> uploaderIds = documents.stream().map(LearningDocument::getUploaderId).toList();
        Map<Long, String> uploaderNameMap = getUploaderNamesMap(uploaderIds);
        return documents.stream()
                .map(doc -> {
                    DocumentSummaryResponse res = mapper.toSummaryResponse(doc);
                    res.setUploaderName(uploaderNameMap.getOrDefault(doc.getUploaderId(), "User " + doc.getUploaderId()));
                    return res;
                })
                .toList();
    }

    public PageResponse<DocumentSummaryResponse> getDocuments(
            String search,
            Long subjectId,
            DocumentCategory category,
            String fileType,
            Double minRating,
            String sortBy,
            int page,
            int size) {

        Sort sort;
        if ("downloads".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "downloadCount");
        } else if ("views".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "viewCount");
        } else if ("ratings".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "averageRating");
        } else {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<LearningDocument> result = learningDocumentRepo.searchDocuments(
                search,
                subjectId,
                category,
                fileType,
                minRating,
                pageable
        );

        List<Long> uploaderIds = result.getContent().stream().map(LearningDocument::getUploaderId).toList();
        Map<Long, String> uploaderNameMap = getUploaderNamesMap(uploaderIds);
        List<DocumentSummaryResponse> content = result.getContent().stream()
                .map(doc -> {
                    DocumentSummaryResponse res = mapper.toSummaryResponse(doc);
                    res.setUploaderName(uploaderNameMap.getOrDefault(doc.getUploaderId(), "User " + doc.getUploaderId()));
                    return res;
                })
                .toList();

        return new PageResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext()
        );
    }

    public String getPreviewUrl(Long id) {
        LearningDocument document = learningDocumentRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Không tìm thấy tài liệu học liệu"));
        if (document.getStatus() != DocumentStatus.PUBLISHED) {
            throw new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Không tìm thấy tài liệu học liệu");
        }
        return document.getFileUrl();
    }

    @Transactional
    public String getDownloadUrl(Long id) {
        LearningDocument document = learningDocumentRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Không tìm thấy tài liệu học liệu"));
        if (document.getStatus() != DocumentStatus.PUBLISHED) {
            throw new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Không tìm thấy tài liệu học liệu");
        }
        document.setDownloadCount(document.getDownloadCount() + 1);
        learningDocumentRepo.save(document);
        return document.getFileUrl();
    }

    @Transactional
    public void bookmarkDocument(Long documentId, Long userId) {
        LearningDocument document = learningDocumentRepo.findById(documentId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Không tìm thấy tài liệu học liệu"));
        if (document.getStatus() != DocumentStatus.PUBLISHED) {
            throw new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Không tìm thấy tài liệu học liệu");
        }
        if (documentBookmarkRepo.existsByDocumentIdAndUserId(documentId, userId)) {
            throw new AppException(ErrorCode.DOCUMENT_ALREADY_BOOKMARKED, "Tài liệu này đã được lưu trước đó");
        }
        DocumentBookmark bookmark = new DocumentBookmark();
        bookmark.setDocument(document);
        bookmark.setUserId(userId);
        documentBookmarkRepo.save(bookmark);
    }

    @Transactional
    public void removeBookmark(Long documentId, Long userId) {
        DocumentBookmark bookmark = documentBookmarkRepo.findByDocumentIdAndUserId(documentId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Không tìm thấy tài liệu đã lưu"));
        documentBookmarkRepo.delete(bookmark);
    }

    public PageResponse<DocumentSummaryResponse> getMyBookmarks(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DocumentBookmark> bookmarksPage = documentBookmarkRepo.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        List<Long> uploaderIds = bookmarksPage.getContent().stream()
                .map(bookmark -> bookmark.getDocument().getUploaderId())
                .toList();
        Map<Long, String> uploaderNameMap = getUploaderNamesMap(uploaderIds);
        List<DocumentSummaryResponse> content = bookmarksPage.getContent().stream()
                .map(bookmark -> {
                    DocumentSummaryResponse res = mapper.toSummaryResponse(bookmark.getDocument());
                    res.setUploaderName(uploaderNameMap.getOrDefault(bookmark.getDocument().getUploaderId(), "User " + bookmark.getDocument().getUploaderId()));
                    return res;
                })
                .toList();
        return new PageResponse<>(
                content,
                bookmarksPage.getNumber(),
                bookmarksPage.getSize(),
                bookmarksPage.getTotalElements(),
                bookmarksPage.getTotalPages(),
                bookmarksPage.hasNext()
        );
    }

    public PageResponse<LearningDocumentResponse> getMyUploadedDocuments(
            Long userId,
            DocumentStatus status,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<LearningDocument> result = learningDocumentRepo.searchDocumentsForAdmin(
                null, status, null, null, userId, null, null, pageable
        );

        List<LearningDocumentResponse> content = result.getContent().stream()
                .map(doc -> {
                    LearningDocumentResponse res = mapper.toResponse(doc);
                    res.setUploaderName(getUploaderName(userId));
                    return res;
                })
                .toList();

        return new PageResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext()
        );
    }

    public boolean isBookmarked(Long documentId, Long userId) {
        return documentBookmarkRepo.existsByDocumentIdAndUserId(documentId, userId);
    }

    @Transactional
    public DocumentRatingResponse rateDocument(Long documentId, Long userId, Integer score, String review) {
        LearningDocument document = learningDocumentRepo.findById(documentId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Không tìm thấy tài liệu học liệu"));
        if (document.getStatus() != DocumentStatus.PUBLISHED) {
            throw new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Không tìm thấy tài liệu học liệu");
        }
        if (document.getUploaderId().equals(userId)) {
            throw new AppException(ErrorCode.DOCUMENT_ACCESS_DENIED, "Người tải lên không thể tự đánh giá tài liệu của mình");
        }

        Optional<DocumentRating> existing = documentRatingRepo.findByDocumentIdAndUserId(documentId, userId);
        DocumentRating rating;
        if (existing.isPresent()) {
            rating = existing.get();
            rating.setScore(score);
            rating.setReview(review);
        } else {
            rating = new DocumentRating();
            rating.setDocument(document);
            rating.setUserId(userId);
            rating.setScore(score);
            rating.setReview(review);
        }
        DocumentRating savedRating = documentRatingRepo.save(rating);
        contentModerationService.moderateDocumentRatingAsync(savedRating.getId());

        Double avg = documentRatingRepo.averageScoreByDocumentId(documentId);
        Long count = documentRatingRepo.countByDocumentId(documentId);
        document.setAverageRating(avg != null ? avg : 0.0);
        document.setRatingCount(count != null ? count : 0L);
        learningDocumentRepo.save(document);

        DocumentRatingResponse response = mapper.toRatingResponse(savedRating);
        enrichRatingResponse(response, userId);
        return response;
    }

    @Transactional
    public void deleteRating(Long documentId, Long userId) {
        LearningDocument document = learningDocumentRepo.findById(documentId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Không tìm thấy tài liệu học liệu"));

        DocumentRating rating = documentRatingRepo.findByDocumentIdAndUserId(documentId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_RATING_NOT_FOUND, "Không tìm thấy đánh giá cho tài liệu này"));

        documentRatingRepo.delete(rating);

        Double avg = documentRatingRepo.averageScoreByDocumentId(documentId);
        Long count = documentRatingRepo.countByDocumentId(documentId);
        document.setAverageRating(avg != null ? avg : 0.0);
        document.setRatingCount(count != null ? count : 0L);
        learningDocumentRepo.save(document);
    }

    public DocumentRatingResponse getMyRating(Long documentId, Long userId) {
        DocumentRating rating = documentRatingRepo.findByDocumentIdAndUserId(documentId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_RATING_NOT_FOUND, "Không tìm thấy đánh giá cho tài liệu này"));
        DocumentRatingResponse response = mapper.toRatingResponse(rating);
        enrichRatingResponse(response, userId);
        return response;
    }

    public boolean existsById(Long documentId) {
        return learningDocumentRepo.existsById(documentId);
    }

    public void reportDocument(Long documentId, Long userId, CreateDocumentReportRequest request) {
        LearningDocument document = learningDocumentRepo.findById(documentId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Không tìm thấy tài liệu học liệu"));
        if (document.getStatus() != DocumentStatus.PUBLISHED) {
            throw new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Không tìm thấy tài liệu học liệu");
        }

        UserServiceClientReportRequest clientRequest = new UserServiceClientReportRequest();
        clientRequest.setTargetType("DOCUMENT");
        clientRequest.setTargetId(documentId);
        clientRequest.setReason(request.getReason().name());
        clientRequest.setDescription(request.getDescription());

        userServiceClient.createReport(userId, clientRequest);
    }

    public PageResponse<AdminDocumentResponse> getDocumentsForAdmin(
            String search,
            DocumentStatus status,
            Long subjectId,
            DocumentCategory category,
            Long uploaderId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String sortBy,
            int page,
            int size) {

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if ("views".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "viewCount");
        } else if ("downloads".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "downloadCount");
        } else if ("ratings".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "averageRating");
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<LearningDocument> result = learningDocumentRepo.searchDocumentsForAdmin(
                search, status, subjectId, category, uploaderId, startDate, endDate, pageable
        );

        List<Long> documentIds = result.getContent().stream()
                .filter(doc -> doc.getStatus() != DocumentStatus.PENDING)
                .map(LearningDocument::getId)
                .toList();
        Map<String, Long> reportCounts = new HashMap<>();
        if (!documentIds.isEmpty()) {
            try {
                var countWrapper = userServiceClient.getUnresolvedReportCounts("DOCUMENT", documentIds);
                if (countWrapper != null && countWrapper.isSuccess() && countWrapper.getData() != null) {
                    reportCounts = countWrapper.getData();
                }
            } catch (Exception e) {
                System.err.println("Failed to fetch unresolved report counts: " + e.getMessage());
            }
        }

        final Map<String, Long> finalCounts = reportCounts;
        List<Long> uploaderIds = result.getContent().stream().map(LearningDocument::getUploaderId).toList();
        Map<Long, String> uploaderNameMap = getUploaderNamesMap(uploaderIds);
        List<AdminDocumentResponse> content = result.getContent().stream()
                .map(doc -> {
                    AdminDocumentResponse res = mapper.toAdminResponse(doc, finalCounts.getOrDefault(doc.getId().toString(), 0L));
                    res.setUploaderName(uploaderNameMap.getOrDefault(doc.getUploaderId(), "User " + doc.getUploaderId()));
                    return res;
                })
                .toList();

        return new PageResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext()
        );
    }

    public AdminDocumentResponse getAdminDocumentDetails(Long id) {
        LearningDocument document = learningDocumentRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Không tìm thấy tài liệu học liệu"));

        Long count = 0L;
        if (document.getStatus() != DocumentStatus.PENDING) {
            try {
                var countWrapper = userServiceClient.getUnresolvedReportCounts("DOCUMENT", List.of(id));
                if (countWrapper != null && countWrapper.isSuccess() && countWrapper.getData() != null) {
                    count = countWrapper.getData().getOrDefault(id.toString(), 0L);
                }
            } catch (Exception e) {
                System.err.println("Failed to fetch unresolved report count: " + e.getMessage());
            }
        }

        AdminDocumentResponse response = mapper.toAdminResponse(document, count);
        response.setUploaderName(getUploaderName(document.getUploaderId()));
        return response;
    }

    @Transactional
    public AdminDocumentResponse approveDocument(Long documentId, Long adminId) {
        LearningDocument document = learningDocumentRepo.findById(documentId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Không tìm thấy tài liệu học liệu"));

        if (document.getStatus() != DocumentStatus.PENDING) {
            throw new AppException(ErrorCode.DOCUMENT_ACCESS_DENIED, "Chỉ có thể duyệt tài liệu ở trạng thái chờ duyệt");
        }

        document.setStatus(DocumentStatus.PUBLISHED);
        document.setPublishedAt(LocalDateTime.now());
        document.setReviewerId(adminId);
        document.setReviewedAt(LocalDateTime.now());
        LearningDocument saved = learningDocumentRepo.save(document);

        auditLogService.log(adminId, "APPROVE_DOCUMENT", documentId.toString(), "DOCUMENT", "Duyệt tài liệu: " + document.getTitle());

        AdminDocumentResponse response = mapper.toAdminResponse(saved, 0L);
        response.setUploaderName(getUploaderName(saved.getUploaderId()));
        return response;
    }

    @Transactional
    public AdminDocumentResponse rejectDocument(Long documentId, Long adminId, String rejectionReason) {
        LearningDocument document = learningDocumentRepo.findById(documentId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Không tìm thấy tài liệu học liệu"));

        if (document.getStatus() != DocumentStatus.PENDING) {
            throw new AppException(ErrorCode.DOCUMENT_ACCESS_DENIED, "Chỉ có thể từ chối tài liệu ở trạng thái chờ duyệt");
        }

        document.setStatus(DocumentStatus.REJECTED);
        document.setRejectionReason(rejectionReason);
        document.setReviewerId(adminId);
        document.setReviewedAt(LocalDateTime.now());
        LearningDocument saved = learningDocumentRepo.save(document);

        auditLogService.log(adminId, "REJECT_DOCUMENT", documentId.toString(), "DOCUMENT", "Từ chối tài liệu: " + document.getTitle() + ". Lý do: " + rejectionReason);

        AdminDocumentResponse response = mapper.toAdminResponse(saved, 0L);
        response.setUploaderName(getUploaderName(saved.getUploaderId()));
        return response;
    }

    @Transactional
    public AdminDocumentResponse hideDocument(Long documentId, Long adminId, String hiddenReason) {
        LearningDocument document = learningDocumentRepo.findById(documentId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Không tìm thấy tài liệu học liệu"));

        if (document.getStatus() != DocumentStatus.PUBLISHED) {
            throw new AppException(ErrorCode.DOCUMENT_ACCESS_DENIED, "Chỉ có thể ẩn tài liệu đang hiển thị");
        }

        document.setStatus(DocumentStatus.HIDDEN);
        document.setHiddenReason(hiddenReason);
        LearningDocument saved = learningDocumentRepo.save(document);

        auditLogService.log(adminId, "HIDE_DOCUMENT", documentId.toString(), "DOCUMENT", "Ẩn tài liệu: " + document.getTitle() + ". Lý do: " + hiddenReason);

        AdminDocumentResponse response = mapper.toAdminResponse(saved, 0L);
        response.setUploaderName(getUploaderName(saved.getUploaderId()));
        return response;
    }

    @Transactional
    public AdminDocumentResponse restoreDocument(Long documentId, Long adminId) {
        LearningDocument document = learningDocumentRepo.findById(documentId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND, "Không tìm thấy tài liệu học liệu"));

        if (document.getStatus() != DocumentStatus.HIDDEN) {
            throw new AppException(ErrorCode.DOCUMENT_ACCESS_DENIED, "Chỉ có thể khôi phục tài liệu đang bị ẩn");
        }

        document.setStatus(DocumentStatus.PUBLISHED);
        document.setHiddenReason(null);
        LearningDocument saved = learningDocumentRepo.save(document);

        auditLogService.log(adminId, "RESTORE_DOCUMENT", documentId.toString(), "DOCUMENT", "Khôi phục tài liệu: " + document.getTitle());

        AdminDocumentResponse response = mapper.toAdminResponse(saved, 0L);
        response.setUploaderName(getUploaderName(saved.getUploaderId()));
        return response;
    }

    private String getUploaderName(Long uploaderId) {
        if (uploaderId == null) return null;
        try {
            var usersResponse = userServiceClient.getBasicUsers(List.of(uploaderId));
            if (usersResponse != null && usersResponse.isSuccess() && usersResponse.getData() != null && !usersResponse.getData().isEmpty()) {
                return usersResponse.getData().get(0).getFullName();
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch uploader name: " + e.getMessage());
        }
        return "User " + uploaderId;
    }

    private Map<Long, String> getUploaderNamesMap(List<Long> uploaderIds) {
        List<Long> filteredIds = uploaderIds == null ? List.of() : uploaderIds.stream().filter(Objects::nonNull).distinct().toList();
        if (filteredIds.isEmpty()) return Map.of();
        try {
            var usersResponse = userServiceClient.getBasicUsers(filteredIds);
            if (usersResponse != null && usersResponse.isSuccess() && usersResponse.getData() != null) {
                return usersResponse.getData().stream()
                        .collect(Collectors.toMap(BasicUserResponse::getUserId, BasicUserResponse::getFullName, (a, b) -> a));
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch uploader names map: " + e.getMessage());
        }
        return Map.of();
    }

    public PageResponse<DocumentRatingResponse> getDocumentRatings(Long documentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<DocumentRating> ratingPage = documentRatingRepo.findByDocumentId(documentId, pageable);

        List<Long> userIds = ratingPage.getContent().stream()
                .map(DocumentRating::getUserId)
                .toList();
        Map<Long, BasicUserResponse> userMap = getUserResponsesMap(userIds);

        List<DocumentRatingResponse> content = ratingPage.getContent().stream()
                .map(rating -> {
                    DocumentRatingResponse res = mapper.toRatingResponse(rating);
                    BasicUserResponse user = userMap.get(rating.getUserId());
                    if (user != null) {
                        res.setUserName(user.getFullName());
                        res.setUserAvatar(user.getAvatarUrl());
                    } else {
                        res.setUserName("User " + rating.getUserId());
                    }
                    return res;
                })
                .toList();

        return new PageResponse<>(
                content,
                ratingPage.getNumber(),
                ratingPage.getSize(),
                ratingPage.getTotalElements(),
                ratingPage.getTotalPages(),
                ratingPage.hasNext()
        );
    }

    private void enrichRatingResponse(DocumentRatingResponse response, Long userId) {
        BasicUserResponse userInfo = getUserInfo(userId);
        if (userInfo != null) {
            response.setUserName(userInfo.getFullName());
            response.setUserAvatar(userInfo.getAvatarUrl());
        } else {
            response.setUserName("User " + userId);
        }
    }

    private BasicUserResponse getUserInfo(Long userId) {
        if (userId == null) return null;
        try {
            var usersResponse = userServiceClient.getBasicUsers(List.of(userId));
            if (usersResponse != null && usersResponse.isSuccess() && usersResponse.getData() != null && !usersResponse.getData().isEmpty()) {
                return usersResponse.getData().get(0);
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch user info: " + e.getMessage());
        }
        return null;
    }

    private Map<Long, BasicUserResponse> getUserResponsesMap(List<Long> userIds) {
        List<Long> filteredIds = userIds == null ? List.of() : userIds.stream().filter(Objects::nonNull).distinct().toList();
        if (filteredIds.isEmpty()) return Map.of();
        try {
            var usersResponse = userServiceClient.getBasicUsers(filteredIds);
            if (usersResponse != null && usersResponse.isSuccess() && usersResponse.getData() != null) {
                return usersResponse.getData().stream()
                        .collect(Collectors.toMap(BasicUserResponse::getUserId, u -> u, (a, b) -> a));
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch user responses map: " + e.getMessage());
        }
        return Map.of();
    }
}
