package com.example.microservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "student_free_time_slots", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_term_day_slot", 
                columnNames = {"user_id", "term_id", "day_of_week", "slot_code"})
})
public class StudentFreeTimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private AcademicTerm term;

    @Column(name = "day_of_week", nullable = false)
    private Byte dayOfWeek; // 0=Mon ... 6=Sun

    @Column(name = "slot_code", nullable = false, length = 5)
    private String slotCode; // ca1, ca2, ca3, ca4, ca5, ca6

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    public StudentFreeTimeSlot() {
    }

    public StudentFreeTimeSlot(Long userId, AcademicTerm term, Byte dayOfWeek, String slotCode) {
        this.userId = userId;
        this.term = term;
        this.dayOfWeek = dayOfWeek;
        this.slotCode = slotCode;
        this.isAvailable = true;
    }

}

