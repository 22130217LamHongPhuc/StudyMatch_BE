package com.example.microservice.feignAPI;

import com.example.microservice.dto.respone.MutualFriendsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
@FeignClient(name = "SOCIAL-SERVICE", url = "${SOCIAL_SERVICE_URL:http://localhost:8083}")
public interface SocialClient {
    @GetMapping("/social/friends/{id}/count")
    public Long getTotalFriends(@PathVariable("id") Long id);

    @GetMapping("/social/friends/{id}/mutual")
    public MutualFriendsDto getMutualFriends(@PathVariable("id") Long id, @RequestParam("targetUserId") Long targetUserId);

    @GetMapping("/api/documents/{documentId}/exists")
    public boolean existsById(@PathVariable("documentId") Long documentId);

    @GetMapping("/api/documents/{documentId}")
    public java.util.Map<String, Object> getDocumentDetails(@PathVariable("documentId") Long documentId);
}
