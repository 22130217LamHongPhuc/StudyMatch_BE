package com.group_service.repository;

import com.group_service.entity.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    List<StudySession> findByGroupIdOrderByStartTimeAsc(Long groupId);
}

