package com.example.microservice.controller;

import com.example.microservice.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class Chat2 {
    @Autowired
    ChatService serivce;
    @GetMapping("/1")

    public ResponseEntity<?> re (){

        return ResponseEntity.ok(serivce.sendMessage(1L));
    }
}
