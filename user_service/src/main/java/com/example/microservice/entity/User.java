package com.example.microservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Size(max = 150)
    @NotNull
    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Size(max = 120)
    @NotNull
    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Size(max = 500)
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Size(max = 30)
    @NotNull
    @ColumnDefault("'student'")
    @Column(name = "role", nullable = false, length = 30)
    private String role;

    @Size(max = 30)
    @NotNull
    @ColumnDefault("'active'")
    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "is_onboarding_completed", nullable = false)
    private Boolean isOnboardingCompleted = false;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @NotNull
    @ColumnDefault("current_timestamp()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ColumnDefault("current_timestamp()")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Lob
    @Column(name = "bio")
    private String bio;

}