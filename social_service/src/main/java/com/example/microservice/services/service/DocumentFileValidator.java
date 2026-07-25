package com.example.microservice.services.service;

import com.example.microservice.services.exception.AppException;
import com.example.microservice.services.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URL;
import java.util.*;

@Component
public class DocumentFileValidator {

    private final Set<String> allowedDomains;
    private final long maxFileSize;
    private final Set<String> allowedTypes;

    private static final Map<String, List<String>> EXT_TO_MIME_MAP = new HashMap<>();

    static {
        EXT_TO_MIME_MAP.put("pdf", List.of("application/pdf"));
        EXT_TO_MIME_MAP.put("docx", List.of("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        EXT_TO_MIME_MAP.put("doc", List.of("application/msword"));
        EXT_TO_MIME_MAP.put("pptx", List.of("application/vnd.openxmlformats-officedocument.presentationml.presentation"));
        EXT_TO_MIME_MAP.put("ppt", List.of("application/vnd.ms-powerpoint"));
        EXT_TO_MIME_MAP.put("xlsx", List.of("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        EXT_TO_MIME_MAP.put("xls", List.of("application/vnd.ms-excel"));
        EXT_TO_MIME_MAP.put("zip", List.of("application/zip", "application/x-zip-compressed"));
        EXT_TO_MIME_MAP.put("rar", List.of("application/x-rar-compressed", "application/vnd.rar"));
        EXT_TO_MIME_MAP.put("txt", List.of("text/plain"));
        EXT_TO_MIME_MAP.put("png", List.of("image/png"));
        EXT_TO_MIME_MAP.put("jpg", List.of("image/jpeg"));
        EXT_TO_MIME_MAP.put("jpeg", List.of("image/jpeg"));
    }

    public DocumentFileValidator(
            @Value("${app.document.allowed-domains}") String allowedDomainsStr,
            @Value("${app.document.max-file-size}") long maxFileSize,
            @Value("${app.document.allowed-types}") String allowedTypesStr) {

        this.maxFileSize = maxFileSize;

        this.allowedDomains = new HashSet<>();
        if (allowedDomainsStr != null && !allowedDomainsStr.trim().isEmpty()) {
            for (String s : allowedDomainsStr.split(",")) {
                this.allowedDomains.add(s.trim().toLowerCase());
            }
        }

        this.allowedTypes = new HashSet<>();
        if (allowedTypesStr != null && !allowedTypesStr.trim().isEmpty()) {
            for (String s : allowedTypesStr.split(",")) {
                this.allowedTypes.add(s.trim().toLowerCase());
            }
        }
    }

    public void validate(
            String fileUrl,
            String storageKey,
            String fileType,
            String mimeType,
            Long fileSize) {

        // 1. URL format & scheme check
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_DOCUMENT_FILE_URL, "File URL must not be empty");
        }

        URI uri;
        try {
            uri = new URI(fileUrl);
            // Verify it has a valid host
            if (uri.getHost() == null) {
                throw new Exception("Invalid host");
            }
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_DOCUMENT_FILE_URL, "File URL is malformed");
        }

        if (!"https".equalsIgnoreCase(uri.getScheme())) {
            throw new AppException(ErrorCode.INVALID_DOCUMENT_FILE_URL, "Only HTTPS URLs are allowed");
        }

        // 2. Domain check
        String host = uri.getHost().toLowerCase();
        boolean domainAllowed = false;
        for (String allowedDomain : allowedDomains) {
            if (host.equals(allowedDomain) || host.endsWith("." + allowedDomain)) {
                domainAllowed = true;
                break;
            }
        }
        if (!domainAllowed) {
            throw new AppException(ErrorCode.INVALID_DOCUMENT_FILE_URL, "Domain not allowed: " + host);
        }

        // 3. File size check
        if (fileSize == null || fileSize <= 0) {
            throw new AppException(ErrorCode.INVALID_DOCUMENT_FILE_SIZE, "File size must be greater than 0");
        }
        if (fileSize > maxFileSize) {
            throw new AppException(ErrorCode.INVALID_DOCUMENT_FILE_SIZE, "File size exceeds the limit of " + maxFileSize + " bytes");
        }

        // 4. File type check
        if (fileType == null || fileType.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_DOCUMENT_FILE_TYPE, "File type must not be empty");
        }
        String normalizedType = fileType.trim().toLowerCase();
        if (!allowedTypes.contains(normalizedType)) {
            throw new AppException(ErrorCode.INVALID_DOCUMENT_FILE_TYPE, "File type not supported: " + fileType);
        }

        // 5. MIME type vs File type check
        if (mimeType == null || mimeType.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_DOCUMENT_FILE_TYPE, "MIME type must not be empty");
        }
        String normalizedMime = mimeType.trim().toLowerCase();
        List<String> validMimes = EXT_TO_MIME_MAP.get(normalizedType);
        if (validMimes != null && !validMimes.contains(normalizedMime)) {
            throw new AppException(ErrorCode.INVALID_DOCUMENT_FILE_TYPE, "MIME type '" + mimeType + "' does not match file type '" + fileType + "'");
        }

        // 6. Storage key sanity check
        if (storageKey != null && !storageKey.trim().isEmpty()) {
            if (storageKey.contains("..") || storageKey.contains("/") || storageKey.contains("\\")) {
                throw new AppException(ErrorCode.INVALID_DOCUMENT_FILE_URL, "Storage key contains invalid characters");
            }
        }
    }
}
