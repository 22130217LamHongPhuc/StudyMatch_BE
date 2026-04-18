package com.example.microservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "message_read_receipts")
public class MessageReadReceipt {
    @Id
    @Column(name = "receipt_id", nullable = false)
    private Long id;


    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;


    @NotNull
    @ColumnDefault("current_timestamp()")
    @Column(name = "read_at", nullable = false)
    private Instant readAt;

}