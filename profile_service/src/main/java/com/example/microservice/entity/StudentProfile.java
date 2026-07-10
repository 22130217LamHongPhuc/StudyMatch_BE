package com.example.microservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "student_profiles", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_id", columnNames = { "user_id" }),
        @UniqueConstraint(name = "uk_student_code", columnNames = { "student_code" })
})
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "student_code", nullable = false, length = 30)
    private String studentCode;

    @Column(name = "full_name", length = 120)
    private String fullName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "age_group", length = 20)
    private String ageGroup;

    @Column(name = "region", length = 100)
    private String region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cohort_id")
    private Cohort cohort;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public StudentProfile() {
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

}
