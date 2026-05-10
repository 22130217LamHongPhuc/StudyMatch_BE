package com.example.microservice.controller;

import com.example.microservice.config.APIResponse;
import com.example.microservice.dto.MessDTO;
import com.example.microservice.handle.ResponseStatus;
import com.example.microservice.services.ChatService;
import com.example.microservice.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/conversation")
@CrossOrigin(origins = "*")
public class Chat2 {
    @Autowired
    ChatService serivce;
    @Autowired
    MessageService messService;
    @GetMapping
    public ResponseEntity<?> getMess(@RequestParam Long currentUser, @RequestParam Long targetUser, @RequestParam Long page){
        boolean exist = serivce.checkExistConver2User(currentUser, targetUser);
        APIResponse<  Map> apiResponse;
        if(!exist){
            apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, null);
            return ResponseEntity.ok(apiResponse);
        }

        Map<String, Object> map = new HashMap<>();
        Long conversationId = serivce.findConvIdByUser(currentUser, targetUser);
        map.put("conversationId", conversationId);
        List<MessDTO> list = messService.getListMess(conversationId, page);
        map.put("listMess", list);
        apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, map);
        return ResponseEntity.ok(apiResponse);
    }







}
