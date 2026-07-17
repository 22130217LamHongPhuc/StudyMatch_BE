package com.example.microservice.services.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "friends")
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "user1_id", nullable = false)
    private Long user1Id;

    @NotNull
    @Column(name = "user2_id", nullable = false)
    private Long user2Id;

    @NotNull
    @Column(name = "created_at", nullable = false, columnDefinition = "datetime(6) default CURRENT_TIMESTAMP(6)")
    private Instant createdAt;

}