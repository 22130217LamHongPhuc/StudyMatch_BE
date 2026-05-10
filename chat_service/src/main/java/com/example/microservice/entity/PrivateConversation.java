package com.example.microservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "private_conversations")
public class PrivateConversation {
    @Id
    @Column(name = "conversation_id", nullable = false)
    private Long id;
    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversations;

    @NotNull
    @Column(name = "user1_id", nullable = false)
    private Long user1Id;

    @NotNull
    @Column(name = "user2_id", nullable = false)
    private Long user2Id;

}