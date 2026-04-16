package com.example.microservice.repositories;
import com.example.microservice.entity.StudentTermProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
public interface StudentTermProfileRepository extends JpaRepository<StudentTermProfile, Long> {
    @Query("SELECT s FROM StudentTermProfile s WHERE s.userId = :userId AND s.term.termId = :termId")
    Optional<StudentTermProfile> findByUserIdAndTermId(@Param("userId") Long userId, @Param("termId") Long termId);
}
