package com.example.microservice.feignAPI;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SOCIAL-SERVICE", contextId = "postClient", url = "${SOCIAL_SERVICE_URL:http://localhost:8083}")
public interface PostClient {

    @GetMapping("/social/posts/{postId}/exists")
    boolean existsById(@PathVariable Long postId);

    @GetMapping("/social/posts/{postId}")
    java.util.Map<String, Object> getPost(@PathVariable("postId") Long postId);
}
