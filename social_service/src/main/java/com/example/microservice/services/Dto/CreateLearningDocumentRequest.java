package com.example.microservice.services.Dto;

import com.example.microservice.services.entity.DocumentCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateLearningDocumentRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String description;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Category is required")
    private DocumentCategory category;

    @NotBlank(message = "File URL is required")
    private String fileUrl;

    private String storageKey;

    @NotBlank(message = "Original file name is required")
    private String originalFileName;

    @NotBlank(message = "File type is required")
    private String fileType;

    @NotBlank(message = "MIME type is required")
    private String mimeType;

    @NotNull(message = "File size is required")
    @Min(value = 1, message = "File size must be greater than 0")
    private Long fileSize;

    private String sourceName;
}
