package com.example.microservice.services.controller;

import com.example.microservice.services.Dto.*;
import com.example.microservice.services.config.APIResponse;
import com.example.microservice.services.config.ResponseStatus;
import com.example.microservice.services.service.PostService;
import com.example.microservice.services.service.SocialMediaUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/social")
@CrossOrigin(origins = "*")
public class PostController {
    @Autowired
    PostService postService;
    @Autowired
    SocialMediaUploadService mediaUploadService;

    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequest request, @RequestParam(required = false) Long viewerId) {
        PostResponse response = postService.createPost(request, viewerId);
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(new APIResponse<>(ResponseStatus.CREATED, response));
    }

    @GetMapping("/posts/user/{userId}")
    public ResponseEntity<?> getProfileFeed(
            @PathVariable Long userId,
            @RequestParam(required = false) Long viewerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<PostResponse> response = postService.getProfileFeed(userId, viewerId, page, size);
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, response));
    }


    @GetMapping("/posts/feed")
    public ResponseEntity<?> getFeed(
            @RequestParam Long viewerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<PostResponse> response = postService.getFeed(viewerId, page, size);
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, response));
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long postId, @RequestBody UpdatePostRequest request) {
        PostResponse response = postService.updatePost(postId, request);
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, response));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, @RequestParam Long actorId) {
        postService.deletePost(postId, actorId);
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, null));
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<?> toggleLike(
            @PathVariable Long postId,
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "LIKE") String reactionType
    ) {
        PostResponse response = postService.toggleReaction(postId, userId, reactionType);
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, response));
    }

    @PostMapping("/posts/{postId}/share")
    public ResponseEntity<?> sharePost(
            @PathVariable Long postId,
            @RequestBody SharePostRequest request,
            @RequestParam(required = false) Long viewerId
    ) {
        PostResponse response = postService.sharePost(postId, request, viewerId);
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(new APIResponse<>(ResponseStatus.CREATED, response));
    }

    @GetMapping("/posts/{postId}/reactions")
    public ResponseEntity<?> getReactions(@PathVariable Long postId, @RequestParam(required = false) Long viewerId) {
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, postService.getPostReactions(postId, viewerId)));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long postId, @RequestBody CreateCommentRequest request) {
        PostCommentDto response = postService.addComment(postId, request);
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(new APIResponse<>(ResponseStatus.CREATED, response));
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, postService.getComments(postId)));
    }

    @GetMapping("/posts/{postId}/exists")
    public ResponseEntity<Boolean> existsById(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.existsById(postId));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId, @RequestParam(required = false) Long viewerId) {
        PostResponse response = postService.getPostById(postId, viewerId);
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, response));
    }

    @GetMapping("/users/{userId}/stats")
    public ResponseEntity<?> getStats(@PathVariable Long userId) {
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, postService.getStats(userId)));
    }

    @GetMapping("/users/{userId}/achievements")
    public ResponseEntity<?> getAchievements(@PathVariable Long userId) {
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, postService.getAchievements(userId)));
    }

    @PostMapping("/posts/media")
    public ResponseEntity<?> uploadPostMedia(@RequestParam("file") MultipartFile file) {
        Map result = mediaUploadService.uploadPostMedia(file);
        String url = String.valueOf(result.get("secure_url"));
        String originalName = file.getOriginalFilename();
        if (originalName != null) {
            try {
                url = url + "?filename=" + java.net.URLEncoder.encode(originalName, "UTF-8");
            } catch (java.io.UnsupportedEncodingException e) {
                // Ignore encoding error
            }
        }
        String resourceType = String.valueOf(result.get("resource_type"));
        String mediaType = "video".equalsIgnoreCase(resourceType) ? "VIDEO" : "IMAGE";
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, Map.of(
                "mediaUrl", url,
                "mediaType", mediaType
        )));
    }
}
