package com.example.microservice.repositories;

import com.example.microservice.dto.response.SubjectInfoResponse;
import com.example.microservice.entity.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("""
        select s from Subject s
        where (:search is null or :search = '' or lower(s.subjectName) like lower(concat('%', :search, '%')) or lower(s.subjectCode) like lower(concat('%', :search, '%')))
    """)
    Page<Subject> searchSubjects(@Param("search") String search, Pageable pageable);
}
