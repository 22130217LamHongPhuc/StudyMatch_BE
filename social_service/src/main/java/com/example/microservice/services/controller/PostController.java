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
    public ResponseEntity<?> getProfileFeed(@PathVariable Long userId, @RequestParam(required = false) Long viewerId) {
        List<PostResponse> response = postService.getProfileFeed(userId, viewerId);
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
    public ResponseEntity<?> toggleLike(@PathVariable Long postId, @RequestParam Long userId) {
        PostResponse response = postService.toggleReaction(postId, userId);
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, response));
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
        String resourceType = String.valueOf(result.get("resource_type"));
        String mediaType = "video".equalsIgnoreCase(resourceType) ? "VIDEO" : "IMAGE";
        return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, Map.of(
                "mediaUrl", url,
                "mediaType", mediaType
        )));
    }
}
