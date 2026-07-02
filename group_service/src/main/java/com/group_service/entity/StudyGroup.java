package com.group_service.entity;

import com.group_service.entity.enums.GroupStatus;
import com.group_service.entity.enums.GroupType;
import com.group_service.entity.enums.GroupVisibility;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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


    @Enumerated(EnumType.STRING)
    @Column(name = "group_type", nullable = false, length = 30)
    private GroupType groupType;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;


    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;


    @Column(name = "owner_user_id")
    private Long ownerUserId;


    @Column(name = "term_id")
    private Long termId;


    @Column(name = "main_subject_id")
    private Long mainSubjectId;

    @Column(name = "subject_name", length = 150)
    private String subjectName;


    @Column(name = "max_members")
    private Integer maxMembers;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private GroupVisibility visibility;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private GroupStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}