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
@Table(name = "student_subject_schedule_slots", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_term_subject_day_slot",
                columnNames = {"user_id", "term_id", "subject_id", "day_of_week", "slot_code"})
})
public class StudentSubjectScheduleSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private AcademicTerm term;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
    @Column(name = "day_of_week", nullable = false)
    private Byte dayOfWeek;
    @Column(name = "slot_code", nullable = false, length = 5)
    private String slotCode;
    @Column(name = "schedule_type", nullable = false, length = 50)
    private String scheduleType = "CURRENT_TERM";
    @Column(name = "location", length = 120)
    private String location;
    @Column(name = "note", length = 255)
    private String note;
    public StudentSubjectScheduleSlot() {
    }

}
