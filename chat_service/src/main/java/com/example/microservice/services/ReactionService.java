package com.example.microservice.services;

import com.example.microservice.entity.Message;
import com.example.microservice.entity.MessageReaction;
import com.example.microservice.repository.ReactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReactionService {
    @Autowired
    ReactionRepo repo;
    @Autowired
    MessageService messService;

    public MessageReaction insertReaction (int messId, String emoji, int userId){
        System.out.println(messId + emoji + userId);
        MessageReaction messageReaction;
        Message mess = messService.getMessById(messId);
        Optional<MessageReaction> reaction = repo.findMessageReactionByMessageAndUserId(mess,(long) userId);
        if(reaction.isPresent()){
            messageReaction = reaction.get();
            messageReaction.setEmoji(emoji);
        }else{
            messageReaction = new MessageReaction();
            messageReaction.setEmoji(emoji);
            messageReaction.setUserId((long) userId);
            messageReaction.setMessage(mess);
            messageReaction.setCreatedAt(LocalDateTime.now());
        }
        System.out.println(messageReaction);
        return  repo.save(messageReaction);
    }

}
