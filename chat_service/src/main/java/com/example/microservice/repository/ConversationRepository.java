package com.example.microservice.repository;



import com.example.microservice.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}