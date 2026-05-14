package com.example.microservice.repository;

import com.example.microservice.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversationRepo extends JpaRepository< Conversation , Long> {
    Optional<Conversation> findConversationById(Long id);


}
