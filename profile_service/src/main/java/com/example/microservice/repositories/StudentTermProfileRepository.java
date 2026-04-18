package com.example.microservice.repositories;
import com.example.microservice.entity.StudentTermProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface StudentTermProfileRepository extends JpaRepository<StudentTermProfile, Long> {
    Optional<StudentTermProfile> findByUserIdAndTerm_TermId(Long userId, Long termId);

    List<StudentTermProfile> findByUserId(Long userId);
}
