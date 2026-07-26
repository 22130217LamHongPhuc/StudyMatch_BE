package com.example.microservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "subjects", uniqueConstraints = {
        @UniqueConstraint(name = "uk_subject_code", columnNames = {"subject_code"})
})
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "subject_code", nullable = false, length = 20)
    private String subjectCode;

    @Column(name = "subject_name", nullable = false, length = 150)
    private String subjectName;

    public Subject() {
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}

