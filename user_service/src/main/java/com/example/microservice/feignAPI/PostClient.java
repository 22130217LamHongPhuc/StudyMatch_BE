package com.example.microservice.feignAPI;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SOCIAL-SERVICE", contextId = "postClient")
public interface PostClient {

    @GetMapping("/social/posts/{postId}/exists")
    boolean existsById(@PathVariable Long postId);
}
