package com.example.microservice.repositories;
import com.example.microservice.dto.response.SubjectInfoResponse;
import com.example.microservice.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    @Query("""
    select new com.example.microservice.dto.response.SubjectInfoResponse(
        s.subjectId,
       s.subjectCode,
        s.subjectName
    )
    from Subject s
    group by s.subjectName
    order by s.subjectName asc
""")
    List<SubjectInfoResponse> getAllSubjects();
}
