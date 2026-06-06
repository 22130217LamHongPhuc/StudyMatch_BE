package com.example.microservice.services.repository;

import com.example.microservice.services.entity.PostReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostReactionRepo extends JpaRepository<PostReaction, Long> {
    Optional<PostReaction> findByPostIdAndUserId(Long postId, Long userId);
    Long countByPostId(Long postId);
    Long countByPostAuthorIdAndPostIsDeletedFalse(Long authorId);

    @Query("select pr.post.id, count(pr) from PostReaction pr where pr.post.id in :postIds group by pr.post.id")
    List<Object[]> countByPostIds(@Param("postIds") List<Long> postIds);
}
