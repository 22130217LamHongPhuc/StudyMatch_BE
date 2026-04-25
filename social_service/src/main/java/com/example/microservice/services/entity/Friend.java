package com.example.microservice.services.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @Column(name = "friend_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "user1_id", nullable = false)
    private Long user1Id;

    @NotNull
    @Column(name = "user2_id", nullable = false)
    private Long user2Id;

    @NotNull
    @ColumnDefault("current_timestamp()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

}