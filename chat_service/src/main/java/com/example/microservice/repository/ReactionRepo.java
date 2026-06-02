package com.example.microservice.repository;

import com.example.microservice.entity.Message;
import com.example.microservice.entity.MessageReaction;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionRepo extends JpaRepository< MessageReaction, Long> {
    Optional<MessageReaction> findMessageReactionByMessageAndUserId(@NotNull Message message, @NotNull Long userId);
    List<MessageReaction> findByMessageIdIn(List<Long> messageIds);
}
