package com.example.microservice.services;

import com.example.microservice.services.Dto.CreateLearningDocumentRequest;
import com.example.microservice.services.Dto.DocumentSummaryResponse;
import com.example.microservice.services.Dto.LearningDocumentResponse;
import com.example.microservice.services.entity.DocumentCategory;
import com.example.microservice.services.entity.DocumentStatus;
import com.example.microservice.services.entity.LearningDocument;
import com.example.microservice.services.exception.AppException;
import com.example.microservice.services.exception.ErrorCode;
import com.example.microservice.services.mapper.LearningDocumentMapper;
import com.example.microservice.services.repository.LearningDocumentRepo;
import com.example.microservice.services.service.DocumentFileValidator;
import com.example.microservice.services.service.LearningDocumentService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LearningDocumentLibraryTest {

    private Validator validator;

    @Spy
    private LearningDocumentMapper mapper = new LearningDocumentMapper();

    @Spy
    private DocumentFileValidator fileValidator = new DocumentFileValidator(
            "cloudinary.com,res.cloudinary.com,storage.googleapis.com",
            52428800L,
            "pdf,docx,pptx"
    );

    @Mock
    private LearningDocumentRepo learningDocumentRepo;

    @InjectMocks
    private LearningDocumentService learningDocumentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    public void testCreateLearningDocumentRequestValidation() {
        CreateLearningDocumentRequest request = new CreateLearningDocumentRequest();
        request.setTitle("Math Textbook");
        request.setSubjectId(101L);
        request.setCategory(DocumentCategory.TEXTBOOK);
        request.setFileUrl("https://res.cloudinary.com/test.pdf");
        request.setOriginalFileName("test.pdf");
        request.setFileType("pdf");
        request.setMimeType("application/pdf");
        request.setFileSize(2048576L);

        Set<ConstraintViolation<CreateLearningDocumentRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());

        request.setTitle(" ");
        violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        request.setTitle("Math Textbook");

        request.setFileSize(0L);
        violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        request.setFileSize(-5L);
        violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testLearningDocumentDefaultValues() throws Exception {
        LearningDocument doc = new LearningDocument();
        doc.setTitle("Textbook");
        doc.setSubjectId(1L);
        doc.setCategory(DocumentCategory.TEXTBOOK);
        doc.setFileUrl("https://res.cloudinary.com/file.pdf");
        doc.setOriginalFileName("file.pdf");
        doc.setFileType("pdf");
        doc.setMimeType("application/pdf");
        doc.setFileSize(100L);
        doc.setUploaderId(12L);

        Method onCreate = LearningDocument.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(doc);

        assertEquals(DocumentStatus.PENDING, doc.getStatus());
        assertEquals(0L, doc.getViewCount());
        assertEquals(0L, doc.getDownloadCount());
        assertEquals(0.0, doc.getAverageRating());
        assertEquals(0L, doc.getRatingCount());
        assertNotNull(doc.getCreatedAt());
        assertNotNull(doc.getUpdatedAt());
        assertNull(doc.getPublishedAt());
    }

    @Test
    public void testMapperToEntity() {
        CreateLearningDocumentRequest request = new CreateLearningDocumentRequest();
        request.setTitle("Math Homework");
        request.setDescription("Solving chapter 3 homework");
        request.setSubjectId(45L);
        request.setCategory(DocumentCategory.EXERCISE);
        request.setFileUrl("https://storage.googleapis.com/homework.pdf");
        request.setStorageKey("google_hw_123");
        request.setOriginalFileName("homework.pdf");
        request.setFileType("pdf");
        request.setMimeType("application/pdf");
        request.setFileSize(1024L);
        request.setSourceName("Prof. John");

        Long uploaderId = 99L;
        LearningDocument doc = mapper.toEntity(request, uploaderId);

        assertNotNull(doc);
        assertEquals("Math Homework", doc.getTitle());
        assertEquals("Solving chapter 3 homework", doc.getDescription());
        assertEquals(45L, doc.getSubjectId());
        assertEquals(DocumentCategory.EXERCISE, doc.getCategory());
        assertEquals("https://storage.googleapis.com/homework.pdf", doc.getFileUrl());
        assertEquals("google_hw_123", doc.getStorageKey());
        assertEquals("homework.pdf", doc.getOriginalFileName());
        assertEquals("pdf", doc.getFileType());
        assertEquals("application/pdf", doc.getMimeType());
        assertEquals(1024L, doc.getFileSize());
        assertEquals("Prof. John", doc.getSourceName());

        assertEquals(uploaderId, doc.getUploaderId());
        assertEquals(DocumentStatus.PENDING, doc.getStatus());
        assertEquals(0L, doc.getViewCount());
        assertEquals(0L, doc.getDownloadCount());
        assertEquals(0.0, doc.getAverageRating());
        assertEquals(0L, doc.getRatingCount());
        assertNull(doc.getRejectionReason());
        assertNull(doc.getPublishedAt());
    }

    @Test
    public void testMapperToResponses() {
        LearningDocument doc = new LearningDocument();
        doc.setId(10L);
        doc.setTitle("Exam Practice");
        doc.setDescription("Mock exams");
        doc.setSubjectId(5L);
        doc.setCategory(DocumentCategory.EXAM);
        doc.setFileUrl("https://res.cloudinary.com/test.docx");
        doc.setStorageKey("cloud_exam_key");
        doc.setOriginalFileName("test.docx");
        doc.setFileType("docx");
        doc.setMimeType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        doc.setFileSize(50000L);
        doc.setUploaderId(77L);
        doc.setSourceName("Self-study");
        doc.setStatus(DocumentStatus.PUBLISHED);
        doc.setCreatedAt(LocalDateTime.now());
        doc.setUpdatedAt(LocalDateTime.now());
        doc.setPublishedAt(LocalDateTime.now());

        LearningDocumentResponse res = mapper.toResponse(doc);
        assertNotNull(res);
        assertEquals(doc.getId(), res.getId());
        assertEquals(doc.getTitle(), res.getTitle());
        assertEquals(doc.getDescription(), res.getDescription());
        assertEquals(doc.getSubjectId(), res.getSubjectId());
        assertEquals(doc.getCategory(), res.getCategory());
        assertEquals(doc.getFileUrl(), res.getFileUrl());
        assertEquals(doc.getStorageKey(), res.getStorageKey());
        assertEquals(doc.getOriginalFileName(), res.getOriginalFileName());
        assertEquals(doc.getFileType(), res.getFileType());
        assertEquals(doc.getMimeType(), res.getMimeType());
        assertEquals(doc.getFileSize(), res.getFileSize());
        assertEquals(doc.getUploaderId(), res.getUploaderId());
        assertEquals(doc.getSourceName(), res.getSourceName());
        assertEquals(doc.getStatus(), res.getStatus());
        assertEquals(doc.getCreatedAt(), res.getCreatedAt());
        assertEquals(doc.getPublishedAt(), res.getPublishedAt());

        DocumentSummaryResponse sum = mapper.toSummaryResponse(doc);
        assertNotNull(sum);
        assertEquals(doc.getId(), sum.getId());
        assertEquals(doc.getTitle(), sum.getTitle());
        assertEquals(doc.getDescription(), sum.getDescription());
        assertEquals(doc.getSubjectId(), sum.getSubjectId());
        assertEquals(doc.getCategory(), sum.getCategory());
        assertEquals(doc.getFileType(), sum.getFileType());
        assertEquals(doc.getFileSize(), sum.getFileSize());
        assertEquals(doc.getUploaderId(), sum.getUploaderId());
        assertEquals(doc.getSourceName(), sum.getSourceName());
        assertEquals(doc.getCreatedAt(), sum.getCreatedAt());
    }

    @Test
    public void testDocumentFileValidator() {
        assertDoesNotThrow(() -> fileValidator.validate(
                "https://res.cloudinary.com/test.pdf",
                "validKey123",
                "pdf",
                "application/pdf",
                1000L
        ));

        AppException ex = assertThrows(AppException.class, () -> fileValidator.validate(
                "http://res.cloudinary.com/test.pdf",
                "validKey123",
                "pdf",
                "application/pdf",
                1000L
        ));
        assertEquals(ErrorCode.INVALID_DOCUMENT_FILE_URL, ex.getErrorCode());
        assertTrue(ex.getMessage().contains("HTTPS"));

        ex = assertThrows(AppException.class, () -> fileValidator.validate(
                "https://malicious.com/test.pdf",
                "validKey123",
                "pdf",
                "application/pdf",
                1000L
        ));
        assertEquals(ErrorCode.INVALID_DOCUMENT_FILE_URL, ex.getErrorCode());
        assertTrue(ex.getMessage().contains("Domain not allowed"));

        ex = assertThrows(AppException.class, () -> fileValidator.validate(
                "https://res.cloudinary.com/test.pdf",
                "validKey123",
                "pdf",
                "application/pdf",
                0L
        ));
        assertEquals(ErrorCode.INVALID_DOCUMENT_FILE_SIZE, ex.getErrorCode());

        ex = assertThrows(AppException.class, () -> fileValidator.validate(
                "https://res.cloudinary.com/test.pdf",
                "validKey123",
                "pdf",
                "application/pdf",
                1000000000L
        ));
        assertEquals(ErrorCode.INVALID_DOCUMENT_FILE_SIZE, ex.getErrorCode());

        ex = assertThrows(AppException.class, () -> fileValidator.validate(
                "https://res.cloudinary.com/test.pdf",
                "validKey123",
                "exe",
                "application/octet-stream",
                1000L
        ));
        assertEquals(ErrorCode.INVALID_DOCUMENT_FILE_TYPE, ex.getErrorCode());

        ex = assertThrows(AppException.class, () -> fileValidator.validate(
                "https://res.cloudinary.com/test.pdf",
                "validKey123",
                "pdf",
                "image/png",
                1000L
        ));
        assertEquals(ErrorCode.INVALID_DOCUMENT_FILE_TYPE, ex.getErrorCode());

        ex = assertThrows(AppException.class, () -> fileValidator.validate(
                "https://res.cloudinary.com/test.pdf",
                "../abnormal/path",
                "pdf",
                "application/pdf",
                1000L
        ));
        assertEquals(ErrorCode.INVALID_DOCUMENT_FILE_URL, ex.getErrorCode());
    }

    @Test
    public void testLearningDocumentServiceUploadDocument() {
        CreateLearningDocumentRequest request = new CreateLearningDocumentRequest();
        request.setTitle("Math Textbook");
        request.setSubjectId(101L);
        request.setCategory(DocumentCategory.TEXTBOOK);
        request.setFileUrl("https://res.cloudinary.com/test.pdf");
        request.setOriginalFileName("test.pdf");
        request.setFileType("pdf");
        request.setMimeType("application/pdf");
        request.setFileSize(2048576L);

        LearningDocument mockSaved = new LearningDocument();
        mockSaved.setId(500L);
        mockSaved.setTitle(request.getTitle());
        mockSaved.setSubjectId(request.getSubjectId());
        mockSaved.setCategory(request.getCategory());
        mockSaved.setFileUrl(request.getFileUrl());
        mockSaved.setOriginalFileName(request.getOriginalFileName());
        mockSaved.setFileType(request.getFileType());
        mockSaved.setMimeType(request.getMimeType());
        mockSaved.setFileSize(request.getFileSize());
        mockSaved.setUploaderId(12L);
        mockSaved.setStatus(DocumentStatus.PENDING);

        when(learningDocumentRepo.save(any(LearningDocument.class))).thenReturn(mockSaved);

        LearningDocumentResponse response = learningDocumentService.uploadDocument(request, 12L);

        assertNotNull(response);
        assertEquals(500L, response.getId());
        assertEquals("Math Textbook", response.getTitle());
        assertEquals(12L, response.getUploaderId());
        assertEquals(DocumentStatus.PENDING, response.getStatus());

        verify(fileValidator, times(1)).validate(any(), any(), any(), any(), any());
        verify(learningDocumentRepo, times(1)).save(any(LearningDocument.class));
    }
}
