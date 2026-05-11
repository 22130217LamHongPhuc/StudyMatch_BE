package com.example.microservice.controller;

import com.example.microservice.config.APIResponse;
import com.example.microservice.dto.ReationRequest;
import com.example.microservice.entity.MessageReaction;
import com.example.microservice.handle.ResponseStatus;
import com.example.microservice.services.ReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping("/messages")
public class MessageController {
    @Autowired
    ReactionService reactionService;
    @PostMapping("/reaction")
    public ResponseEntity<?> insertReation (@RequestBody ReationRequest req){
        System.out.println(req);
    MessageReaction reaction= reactionService.insertReaction(req.getMessageID(),req.getEmoji(), req.getCurrentUser() );
    return ResponseEntity.ok(new APIResponse<>(ResponseStatus.SUCCESS, reaction));
    }



}
