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
    FriendService friendService;
    @Autowired
    UserServiceClient userServiceClient;
    @Autowired
    ContentModerationService contentModerationService;


    @Transactional
    public PostResponse createPost(CreatePostRequest request, Long viewerId) {
        if (request.getAuthorId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "authorId is required");
        }
        // contentModerationService.validateText(request.getContent());
        Post post = new Post();
        post.setAuthorId(request.getAuthorId());
        post.setContent(normalizeText(request.getContent()));
        post.setVisibility(normalizeVisibility(request.getVisibility()));
        applyMedia(post, request.getMedia());
        return toResponse(postRepo.save(post), viewerId, userMap(List.of(request.getAuthorId())));
    }

    @Transactional
    public PostResponse sharePost(Long postId, SharePostRequest request, Long viewerId) {
        if (request.getAuthorId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "authorId is required");
        }
        Post originalPost = getActivePost(postId);
        
        Post post = new Post();
        post.setAuthorId(request.getAuthorId());
        post.setContent(normalizeText(request.getContent()));
        post.setVisibility(normalizeVisibility(request.getVisibility()));
        post.setSharedPost(originalPost);
        
        Post saved = postRepo.save(post);
        
        List<Long> authorIds = new ArrayList<>();
        authorIds.add(request.getAuthorId());
        authorIds.add(originalPost.getAuthorId());
        if (originalPost.getSharedPost() != null) {
            authorIds.add(originalPost.getSharedPost().getAuthorId());
        }
        
        return toResponse(saved, viewerId, userMap(authorIds));
    }

    public PageResponse<PostResponse> getProfileFeed(Long profileUserId, Long viewerId, int page, int size) {
        if (page < 0 || size < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page must be >= 0 and size must be > 0");
        }

        Page<Post> posts = postRepo.findVisibleProfilePosts(profileUserId, viewerId, PageRequest.of(page, size));
        List<Long> authorIds = new ArrayList<>();
        for (Post p : posts.getContent()) {
            authorIds.add(p.getAuthorId());
            if (p.getSharedPost() != null) {
                authorIds.add(p.getSharedPost().getAuthorId());
            }
        }
        Map<Long, BasicUserResponse> users = userMap(authorIds.stream().distinct().toList());
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


    public PageResponse<PostResponse> getFeed(Long viewerId, int page, int size) {
        if (viewerId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "viewerId is required");
        }
        if (page < 0 || size < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page must be >= 0 and size must be > 0");
        }

        Page<Post> posts = postRepo.findVisibleFeedPosts(viewerId, PageRequest.of(page, size));
        List<Long> authorIds = new ArrayList<>();
        for (Post p : posts.getContent()) {
            authorIds.add(p.getAuthorId());
            if (p.getSharedPost() != null) {
                authorIds.add(p.getSharedPost().getAuthorId());
            }
        }
        Map<Long, BasicUserResponse> users = userMap(authorIds.stream().distinct().toList());

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
        // contentModerationService.validateText(request.getContent());
        post.setContent(normalizeText(request.getContent()));
        post.setVisibility(normalizeVisibility(request.getVisibility()));
        post.getMedia().clear();
        applyMedia(post, request.getMedia());
        Post saved = postRepo.save(post);
        List<Long> authorIds = new ArrayList<>();
        authorIds.add(saved.getAuthorId());
        if (saved.getSharedPost() != null) {
            authorIds.add(saved.getSharedPost().getAuthorId());
        }
        return toResponse(saved, request.getActorId(), userMap(authorIds));
    }

    @Transactional
    public void deletePost(Long postId, Long actorId) {
        Post post = getActivePost(postId);
        assertOwner(post, actorId);
        post.setIsDeleted(true);
        postRepo.save(post);
    }

    @Transactional
    public PostResponse toggleReaction(Long postId, Long userId, String reactionType) {
        if (userId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        Post post = getActivePost(postId);
        Optional<PostReaction> existed = reactionRepo.findByPostIdAndUserId(postId, userId);
        String finalReactionType = reactionType != null ? reactionType.trim().toUpperCase() : "LIKE";
        if (existed.isPresent()) {
            PostReaction existingReaction = existed.get();
            if (existingReaction.getReactionType().equalsIgnoreCase(finalReactionType)) {
                reactionRepo.delete(existingReaction);
            } else {
                existingReaction.setReactionType(finalReactionType);
                reactionRepo.save(existingReaction);
            }
        } else {
            PostReaction reaction = new PostReaction();
            reaction.setPost(post);
            reaction.setUserId(userId);
            reaction.setReactionType(finalReactionType);
            reactionRepo.save(reaction);
        }
        List<Long> authorIds = new ArrayList<>();
        authorIds.add(post.getAuthorId());
        if (post.getSharedPost() != null) {
            authorIds.add(post.getSharedPost().getAuthorId());
        }
        return toResponse(post, userId, userMap(authorIds));
    }

    public List<PostReactionResponse> getPostReactions(Long postId, Long viewerId) {
        List<PostReaction> reactions = reactionRepo.findByPostId(postId);
        List<Long> userIds = reactions.stream().map(PostReaction::getUserId).toList();
        Map<Long, BasicUserResponse> users = userMap(userIds);

        List<PostReactionResponse> responses = new ArrayList<>();
        for (PostReaction r : reactions) {
            BasicUserResponse u = users.get(r.getUserId());
            if (u == null) continue;

            boolean isFriend = false;
            int mutualCount = 0;
            if (viewerId != null && !viewerId.equals(r.getUserId())) {
                isFriend = friendRepo.isFriends(viewerId, r.getUserId()) > 0;
                Long count = friendRepo.countMutualFriend(viewerId, r.getUserId());
                mutualCount = count != null ? count.intValue() : 0;
            }

            responses.add(new PostReactionResponse(
                    r.getUserId(),
                    u.getFullName(),
                    u.getAvatarUrl(),
                    r.getReactionType(),
                    isFriend,
                    mutualCount
            ));
        }
        return responses;
    }

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
        contentModerationService.moderatePostCommentAsync(saved.getId());
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
                friendService.totalFriend(userId)
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

    public boolean existsById(Long postId) {
        if (postId == null) return false;
        return postRepo.findById(postId)
                .map(post -> !Boolean.TRUE.equals(post.getIsDeleted()))
                .orElse(false);
    }

    public PostResponse getPostById(Long postId, Long viewerId) {
        Post post = getActivePost(postId);
        return toResponse(post, viewerId, userMap(List.of(post.getAuthorId())));
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
        Optional<PostReaction> reactionOpt = viewerId != null
                ? reactionRepo.findByPostIdAndUserId(post.getId(), viewerId)
                : Optional.empty();
        boolean likedByViewer = reactionOpt.isPresent();
        String reactionType = reactionOpt.map(PostReaction::getReactionType).orElse(null);

        List<PostReaction> allReactions = reactionRepo.findByPostId(post.getId());
        List<String> topReactions = allReactions.stream()
                .filter(r -> r.getReactionType() != null)
                .collect(Collectors.groupingBy(PostReaction::getReactionType, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(2)
                .map(Map.Entry::getKey)
                .toList();

        PostResponse sharedPostRes = null;
        if (post.getSharedPost() != null) {
            sharedPostRes = toResponse(post.getSharedPost(), viewerId, users);
        }

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
                (long) allReactions.size(),
                commentRepo.countByPostIdAndIsDeletedFalse(post.getId()),
                likedByViewer,
                reactionType,
                topReactions,
                sharedPostRes
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
                comment.getCreatedAt(),
                comment.getModerationStatus()
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
