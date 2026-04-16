package com.example.microservice.controller;

import com.example.microservice.config.APIResponse;
import com.example.microservice.dto.MessDTO;
import com.example.microservice.handle.ResponseStatus;
import com.example.microservice.services.ChatService;
import com.example.microservice.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/conversation")
public class Chat2 {
    @Autowired
    ChatService serivce;
    @Autowired
    MessageService messService;
    @GetMapping
    public ResponseEntity<?> getMess(@RequestParam Long conversationId, @RequestParam Long page){
        List<MessDTO> list = messService.getListMess(conversationId, page);
        APIResponse<  List<MessDTO>> apiResponse = new APIResponse<>(ResponseStatus.SUCCESS, list);
        return ResponseEntity.ok(apiResponse);
    }

}
