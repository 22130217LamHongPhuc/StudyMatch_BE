package com.example.microservice.repositories;
import com.example.microservice.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByStudentCode(String studentCode);
    Optional<StudentProfile> findByUserId(Long userId);
}
