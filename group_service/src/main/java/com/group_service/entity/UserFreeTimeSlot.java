package com.group_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "user_free_time_slots",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_term_day_time",
                columnNames = {
                        "user_id",
                        "term_id",
                        "day_of_week",
                        "start_time",
                        "end_time"
                }
        ),
        indexes = {
                @Index(name = "idx_user_free_time_user", columnList = "user_id"),
                @Index(name = "idx_user_free_time_day", columnList = "day_of_week"),
                @Index(name = "idx_user_free_time_term", columnList = "term_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFreeTimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "term_id")
    private Long termId;

    /**
     * 0 = Monday, 1 = Tuesday, ..., 6 = Sunday
     */
    @Column(name = "day_of_week", nullable = false, columnDefinition = "TINYINT")
    private Byte dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "is_available", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isAvailable;

    @Column(length = 255)
    private String note;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}