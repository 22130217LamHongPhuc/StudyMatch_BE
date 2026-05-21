package com.group_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "group_free_time_slots",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_group_term_day_slot",
                columnNames = {"group_id", "term_id", "day_of_week", "slot_code"}
        )
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

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "term_id", nullable = false)
    private Long termId;

    /** 0=Mon ... 6=Sun */
    @Column(name = "day_of_week", nullable = false, columnDefinition = "TINYINT")
    private Byte dayOfWeek;

    @Column(name = "slot_code", nullable = false, length = 5)
    private String slotCode;

    @Column(name = "is_available", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isAvailable;
}
