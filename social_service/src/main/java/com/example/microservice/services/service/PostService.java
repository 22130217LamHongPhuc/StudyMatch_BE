package com.example.microservice.services.service;

import com.example.microservice.services.Dto.*;
import com.example.microservice.services.client.UserServiceClient;
import com.example.microservice.services.entity.Post;
import com.example.microservice.services.entity.PostComment;
import com.example.microservice.services.entity.PostMedia;
import com.example.microservice.services.entity.PostReaction;
import com.example.microservice.services.repository.FriendRepo;
import com.example.microservice.services.repository.PostCommentRepo;
import com.example.microservice.services.repository.PostReactionRepo;
import com.example.microservice.services.repository.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {
    private static final int MAX_POST_MEDIA = 10;

    @Autowired
    PostRepo postRepo;
    @Autowired
    PostReactionRepo reactionRepo;
    @Autowired
    PostCommentRepo commentRepo;
    @Autowired
    FriendRepo friendRepo;
    @Autowired
    UserServiceClient userServiceClient;

    @Transactional
    public PostResponse createPost(CreatePostRequest request, Long viewerId) {
        if (request.getAuthorId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "authorId is required");
        }
        Post post = new Post();
        post.setAuthorId(request.getAuthorId());
        post.setContent(normalizeText(request.getContent()));
        post.setVisibility(normalizeVisibility(request.getVisibility()));
        applyMedia(post, request.getMedia());
        return toResponse(postRepo.save(post), viewerId, userMap(List.of(request.getAuthorId())));
    }

    public List<PostResponse> getProfileFeed(Long profileUserId, Long viewerId) {
        List<Post> posts = postRepo.findByAuthorIdAndIsDeletedFalseOrderByCreatedAtDesc(profileUserId);
        Map<Long, BasicUserResponse> users = userMap(posts.stream().map(Post::getAuthorId).distinct().toList());
        return posts.stream()
                .filter(post -> canViewPost(post, viewerId))
                .map(post -> toResponse(post, viewerId, users))
                .toList();
    }

    public PageResponse<PostResponse> getFeed(Long viewerId, int page, int size) {
        if (viewerId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "viewerId is required");
        }
        if (page < 0 || size < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page must be >= 0 and size must be > 0");
        }

        Page<Post> posts = postRepo.findVisibleFeedPosts(viewerId, PageRequest.of(page, size));
        Map<Long, BasicUserResponse> users = userMap(
                posts.getContent().stream()
                        .map(Post::getAuthorId)
                        .distinct()
                        .toList()
        );

        return new PageResponse<>(
                posts.getContent().stream()
                        .map(post -> toResponse(post, viewerId, users))
                        .toList(),
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalElements(),
                posts.getTotalPages(),
                posts.hasNext()
        );
    }

    @Transactional
    public PostResponse updatePost(Long postId, UpdatePostRequest request) {
        Post post = getActivePost(postId);
        assertOwner(post, request.getActorId());
        post.setContent(normalizeText(request.getContent()));
        post.setVisibility(normalizeVisibility(request.getVisibility()));
        post.getMedia().clear();
        applyMedia(post, request.getMedia());
        return toResponse(postRepo.save(post), request.getActorId(), userMap(List.of(post.getAuthorId())));
    }

    @Transactional
    public void deletePost(Long postId, Long actorId) {
        Post post = getActivePost(postId);
        assertOwner(post, actorId);
        post.setIsDeleted(true);
        postRepo.save(post);
    }

    @Transactional
    public PostResponse toggleReaction(Long postId, Long userId) {
        if (userId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        Post post = getActivePost(postId);
        Optional<PostReaction> existed = reactionRepo.findByPostIdAndUserId(postId, userId);
        if (existed.isPresent()) {
            reactionRepo.delete(existed.get());
        } else {
            PostReaction reaction = new PostReaction();
            reaction.setPost(post);
            reaction.setUserId(userId);
            reaction.setReactionType("LIKE");
            reactionRepo.save(reaction);
        }
        return toResponse(post, userId, userMap(List.of(post.getAuthorId())));
    }

    @Transactional
    public PostCommentDto addComment(Long postId, CreateCommentRequest request) {
        if (request.getAuthorId() == null || normalizeText(request.getContent()) == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "authorId and content are required");
        }
        Post post = getActivePost(postId);
        PostComment comment = new PostComment();
        comment.setPost(post);
        comment.setAuthorId(request.getAuthorId());
        comment.setContent(normalizeText(request.getContent()));
        PostComment saved = commentRepo.save(comment);
        return toCommentDto(saved, userMap(List.of(saved.getAuthorId())));
    }

    public List<PostCommentDto> getComments(Long postId) {
        List<PostComment> comments = commentRepo.findByPostIdAndIsDeletedFalseOrderByCreatedAtAsc(postId);
        Map<Long, BasicUserResponse> users = userMap(comments.stream().map(PostComment::getAuthorId).distinct().toList());
        return comments.stream().map(comment -> toCommentDto(comment, users)).toList();
    }

    public ProfileSocialStatsResponse getStats(Long userId) {
        return new ProfileSocialStatsResponse(
                postRepo.countByAuthorIdAndIsDeletedFalse(userId),
                reactionRepo.countByPostAuthorIdAndPostIsDeletedFalse(userId),
                commentRepo.countByPostAuthorIdAndIsDeletedFalseAndPostIsDeletedFalse(userId),
                friendRepo.countTotalFriend(userId)
        );
    }

    public List<AchievementDto> getAchievements(Long userId) {
        ProfileSocialStatsResponse stats = getStats(userId);
        return List.of(
                achievement("FIRST_POST", "Bai viet dau tien", "Dang bai viet dau tien", stats.getPostCount(), 1L),
                achievement("ACTIVE_WRITER", "Nguoi viet nang dong", "Dat 5 bai viet tren ban tin", stats.getPostCount(), 5L),
                achievement("SOCIAL_STARTER", "Ket noi ban be", "Co 3 nguoi ban", stats.getFriendCount(), 3L),
                achievement("WELL_RECEIVED", "Duoc yeu thich", "Dat 10 luot thich", stats.getLikeCount(), 10L)
        );
    }

    private AchievementDto achievement(String code, String title, String description, Long progress, Long target) {
        long current = progress == null ? 0 : progress;
        return new AchievementDto(code, title, description, current >= target, current, target);
    }

    private Post getActivePost(Long postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        if (Boolean.TRUE.equals(post.getIsDeleted())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        return post;
    }

    private void assertOwner(Post post, Long actorId) {
        if (actorId == null || !actorId.equals(post.getAuthorId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only owner can modify this post");
        }
    }

    private boolean canViewPost(Post post, Long viewerId) {
        if (post == null) return false;
        if (viewerId != null && viewerId.equals(post.getAuthorId())) return true;
        String visibility = normalizeVisibility(post.getVisibility());
        if ("PUBLIC".equals(visibility)) return true;
        if ("FRIENDS".equals(visibility)) {
            return viewerId != null && friendRepo.isFriends(post.getAuthorId(), viewerId) > 0;
        }
        return false;
    }

    private void applyMedia(Post post, List<PostMediaRequest> mediaRequests) {
        if (mediaRequests == null) return;
        if (mediaRequests.size() > MAX_POST_MEDIA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum 10 media items are allowed");
        }
        int index = 0;
        for (PostMediaRequest item : mediaRequests) {
            if (item == null || normalizeText(item.getMediaUrl()) == null) continue;
            PostMedia media = new PostMedia();
            media.setPost(post);
            media.setMediaUrl(item.getMediaUrl().trim());
            media.setMediaType(normalizeMediaType(item.getMediaType()));
            media.setDisplayOrder(index++);
            post.getMedia().add(media);
        }
    }

    private PostResponse toResponse(Post post, Long viewerId, Map<Long, BasicUserResponse> users) {
        BasicUserResponse author = users.get(post.getAuthorId());
        return new PostResponse(
                post.getId(),
                post.getAuthorId(),
                author != null ? author.getFullName() : "User " + post.getAuthorId(),
                author != null ? author.getAvatarUrl() : null,
                post.getContent(),
                post.getVisibility(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getMedia().stream()
                        .map(media -> new PostMediaDto(media.getId(), media.getMediaUrl(), media.getMediaType()))
                        .toList(),
                reactionRepo.countByPostId(post.getId()),
                commentRepo.countByPostIdAndIsDeletedFalse(post.getId()),
                viewerId != null && reactionRepo.findByPostIdAndUserId(post.getId(), viewerId).isPresent()
        );
    }

    private PostCommentDto toCommentDto(PostComment comment, Map<Long, BasicUserResponse> users) {
        BasicUserResponse author = users.get(comment.getAuthorId());
        return new PostCommentDto(
                comment.getId(),
                comment.getAuthorId(),
                author != null ? author.getFullName() : "User " + comment.getAuthorId(),
                author != null ? author.getAvatarUrl() : null,
                comment.getContent(),
                comment.getCreatedAt()
        );
    }

    private Map<Long, BasicUserResponse> userMap(List<Long> ids) {
        List<Long> userIds = ids == null ? List.of() : ids.stream().filter(Objects::nonNull).distinct().toList();
        if (userIds.isEmpty()) return Map.of();
        try {
            List<BasicUserResponse> users = userServiceClient.getBasicUsers(userIds).getData();
            if (users == null) return Map.of();
            return users.stream().collect(Collectors.toMap(BasicUserResponse::getUserId, user -> user, (a, b) -> a));
        } catch (Exception ex) {
            ex.printStackTrace();
            return Map.of();
        }
    }

    private String normalizeText(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        return value.trim();
    }

    private String normalizeVisibility(String value) {
        String normalized = normalizeText(value);
        if (normalized == null) return "PUBLIC";
        String upper = normalized.toUpperCase();
        if (!Set.of("PUBLIC", "FRIENDS", "PRIVATE").contains(upper)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid post visibility");
        }
        return upper;
    }

    private String normalizeMediaType(String value) {
        String normalized = normalizeText(value);
        return normalized == null ? "IMAGE" : normalized.toUpperCase();
    }
}
