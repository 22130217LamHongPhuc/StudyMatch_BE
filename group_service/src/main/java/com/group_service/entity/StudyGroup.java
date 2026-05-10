package com.group_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "study_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Lob
    private String description;

    @Column(name = "owner_user_id", nullable = false)
    private Long ownerUserId;

    @Column(name = "term_id", nullable = false)
    private Long termId;

    @Column(name = "main_subject_id", nullable = false)
    private Long mainSubjectId;

    @Column(name = "subject_name", length = 150)
    private String subjectName;

    @Column(name = "study_goal", length = 50)
    private String studyGoal;

    @Column(name = "study_mode", length = 50)
    private String studyMode;

    @Column(name = "max_members", nullable = false)
    private Integer maxMembers;

    @Column(nullable = false, length = 30)
    private String visibility;

    @Column(nullable = false, length = 30)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


}

