package com.example.microservice.services.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private Long authorId;
    private String authorName;
    private String authorAvatarUrl;
    private String content;
    private String visibility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PostMediaDto> media;
    private Long likeCount;
    private Long commentCount;
    private boolean likedByViewer;
    private String reactionType;
    private List<String> topReactions;
}
