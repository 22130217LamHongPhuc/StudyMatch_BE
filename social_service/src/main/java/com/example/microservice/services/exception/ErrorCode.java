package com.example.microservice.services.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    DOCUMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "DOCUMENT_NOT_FOUND", "Không tìm thấy tài liệu học liệu"),
    DOCUMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "DOCUMENT_ACCESS_DENIED", "Bạn không có quyền truy cập tài liệu này"),
    DOCUMENT_INVALID_STATUS(HttpStatus.BAD_REQUEST, "DOCUMENT_INVALID_STATUS", "Trạng thái tài liệu không hợp lệ"),
    DOCUMENT_ALREADY_BOOKMARKED(HttpStatus.BAD_REQUEST, "DOCUMENT_ALREADY_BOOKMARKED", "Tài liệu này đã được lưu trước đó"),
    DOCUMENT_RATING_NOT_FOUND(HttpStatus.NOT_FOUND, "DOCUMENT_RATING_NOT_FOUND", "Không tìm thấy đánh giá cho tài liệu này"),
    DOCUMENT_ALREADY_REPORTED(HttpStatus.BAD_REQUEST, "DOCUMENT_ALREADY_REPORTED", "Tài liệu này đã được báo cáo trước đó"),
    INVALID_DOCUMENT_FILE_URL(HttpStatus.BAD_REQUEST, "INVALID_DOCUMENT_FILE_URL", "Đường dẫn URL file tài liệu không hợp lệ"),
    INVALID_DOCUMENT_FILE_TYPE(HttpStatus.BAD_REQUEST, "INVALID_DOCUMENT_FILE_TYPE", "Định dạng file tài liệu không hợp lệ"),
    INVALID_DOCUMENT_FILE_SIZE(HttpStatus.BAD_REQUEST, "INVALID_DOCUMENT_FILE_SIZE", "Dung lượng file tài liệu không hợp lệ"),
    INVALID_DOCUMENT_CATEGORY(HttpStatus.BAD_REQUEST, "INVALID_DOCUMENT_CATEGORY", "Danh mục tài liệu không hợp lệ"),
    CONTENT_VIOLATION(HttpStatus.BAD_REQUEST, "CONTENT_VIOLATION", "Nội dung chứa thông tin không lành mạnh/bậy bạ"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "Đã xảy ra lỗi hệ thống không mong muốn");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
