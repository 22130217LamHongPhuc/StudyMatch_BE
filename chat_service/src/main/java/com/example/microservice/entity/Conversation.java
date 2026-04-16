package com.example.microservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Data
@Table(name = "conversations")
public class Conversation {
    @Id
    @Column(name = "conversation_id", nullable = false)
    private Long id;
    @Size(max = 20)
    @NotNull
    @Column(name = "conversation_type", nullable = false, length = 20)
    private String conversationType;

    @NotNull
    @ColumnDefault("current_timestamp()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

}