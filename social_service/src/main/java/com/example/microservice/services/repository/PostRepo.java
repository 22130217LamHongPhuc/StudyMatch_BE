package com.example.microservice.services.repository;

import com.example.microservice.services.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepo extends JpaRepository<Post, Long> {
    List<Post> findByAuthorIdAndIsDeletedFalseOrderByCreatedAtDesc(Long authorId);
    Long countByAuthorIdAndIsDeletedFalse(Long authorId);
}
