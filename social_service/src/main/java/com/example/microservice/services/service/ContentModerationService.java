package com.example.microservice.services.service;

import com.example.microservice.services.exception.AppException;
import com.example.microservice.services.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Locale;

@Service
public class ContentModerationService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${moderation.api-url:http://localhost:8001/moderate/messages}")
    private String moderationApiUrl;

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.context.annotation.Lazy
    private com.example.microservice.services.repository.PostCommentRepo postCommentRepo;

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.context.annotation.Lazy
    private com.example.microservice.services.repository.DocumentRatingRepo documentRatingRepo;

    public String getModerationLabel(String text) {
        if (text == null || text.trim().isBlank()) {
            return "NONE";
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            ModerationRequest requestItem = new ModerationRequest(1L, text);
            HttpEntity<List<ModerationRequest>> entity = new HttpEntity<>(
                    List.of(requestItem),
                    headers
            );

            ResponseEntity<List<ModerationResponse>> response = restTemplate.exchange(
                    moderationApiUrl,
                    org.springframework.http.HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<List<ModerationResponse>>() {}
            );

            List<ModerationResponse> body = response.getBody();
            if (body != null && !body.isEmpty()) {
                String label = body.get(0).getLabel();
                if (label != null) {
                    return label.trim().toUpperCase(Locale.ROOT);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "NONE";
    }

    @org.springframework.scheduling.annotation.Async
    public void moderatePostCommentAsync(Long commentId) {
        try {
            com.example.microservice.services.entity.PostComment comment = postCommentRepo.findById(commentId).orElse(null);
            if (comment == null || comment.getContent() == null || comment.getContent().isBlank()) {
                return;
            }
            String label = getModerationLabel(comment.getContent());
            if (label != null) {
                comment.setModerationStatus(label);
                postCommentRepo.save(comment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @org.springframework.scheduling.annotation.Async
    public void moderateDocumentRatingAsync(Long ratingId) {
        try {
            com.example.microservice.services.entity.DocumentRating rating = documentRatingRepo.findById(ratingId).orElse(null);
            if (rating == null || rating.getReview() == null || rating.getReview().isBlank()) {
                return;
            }
            String label = getModerationLabel(rating.getReview());
            if (label != null) {
                rating.setModerationStatus(label);
                documentRatingRepo.save(rating);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void validateText(String text) {
        // AI moderation check disabled for post and document creation
    }

    public void validateTexts(String... texts) {
        // AI moderation check disabled for post and document creation
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModerationRequest {
        private Long id;
        private String content;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModerationResponse {
        private Long id;
        private String content;
        private String label;
    }
}