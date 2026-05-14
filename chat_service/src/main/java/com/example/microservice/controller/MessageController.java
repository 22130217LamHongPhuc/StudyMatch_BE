package com.example.microservice.controller;

import com.example.microservice.dto.TokenValidateResponse;
import com.example.microservice.entity.Conversation;
import com.example.microservice.entity.Message;
import com.example.microservice.feignClient.UserClient;
import com.example.microservice.services.CloudinaryService;
import com.example.microservice.services.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/messages")
public class MessageController {
    @Autowired
    UserClient client;
    @Autowired
    CloudinaryService cloudinaryService;
    @Autowired
    ConversationService conversationService;


    @PostMapping("/media")
    public ResponseEntity<?> uploadMedia (@RequestParam("file") MultipartFile file,
                                          @RequestParam("conversationID") Long conversationId,
                                         @RequestParam("type") String type,
                                          @RequestHeader("Authorization") String authorization)  {
        TokenValidateResponse response= client.validateToken(authorization);
        if(!response.isValid()){
            throw new IllegalArgumentException("Invalid token");
        }
        Long userId = response.getUserId();
        Map result = cloudinaryService.uploadFile(file);
        String fileUrl = result.get("secure_url").toString();
        String fileName = result.get("display_name").toString();
        String resourceType = result.get("resource_type").toString();
        String format = result.get("format").toString();
        String fileType = resourceType + "/" + format;
        Long fileSize = Long.valueOf(result.get("bytes").toString());
        System.out.println(result + "upload ảnh nè");
        Conversation conversation = conversationService.findById(conversationId);
        if(conversation == null){
            throw new RuntimeException("conversation không tồn tại");

        }


        Message mess = new Message();
        mess.setContent(null);
        mess.setSenderId(userId);
        mess.setConversation(conversation);
        mess.setType(type);
        mess.setCreatedAt(LocalDateTime.now());
        mess.setMediaUrl(fileUrl);
        mess.setIsDeleted(false);
        mess.setIsEdited(false);

        return ResponseEntity.ok(mess);

    }


}
