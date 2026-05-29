package com.example.microservice.services;

import com.example.microservice.entity.Conversation;
import com.example.microservice.repository.ConversationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConversationService {
    @Autowired
    ConversationRepo repo;

    public Conversation save(Conversation conversation){
     return    repo.save(conversation);
    }

     public Conversation findById(Long conversationId){
         Optional<Conversation> id = repo.findConversationById(conversationId);
         if(id.isEmpty()) return null;
         return id.get();
     }

}
