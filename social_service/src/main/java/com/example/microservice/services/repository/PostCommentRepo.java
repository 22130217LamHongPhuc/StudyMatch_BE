package com.example.microservice.services.repository;

import com.example.microservice.services.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostCommentRepo extends JpaRepository<PostComment, Long> {
    List<PostComment> findByPostIdAndIsDeletedFalseOrderByCreatedAtAsc(Long postId);
    Long countByPostIdAndIsDeletedFalse(Long postId);
    Long countByPostAuthorIdAndIsDeletedFalseAndPostIsDeletedFalse(Long authorId);

    @Query("select pc.post.id, count(pc) from PostComment pc where pc.post.id in :postIds and pc.isDeleted = false group by pc.post.id")
    List<Object[]> countByPostIds(@Param("postIds") List<Long> postIds);
}
