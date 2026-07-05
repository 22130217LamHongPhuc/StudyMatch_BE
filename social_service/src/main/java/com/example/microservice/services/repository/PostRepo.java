package com.example.microservice.services.repository;

import com.example.microservice.services.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepo extends JpaRepository<Post, Long> {
    List<Post> findByAuthorIdAndIsDeletedFalseOrderByCreatedAtDesc(Long authorId);
    Long countByAuthorIdAndIsDeletedFalse(Long authorId);

    @Query("""
        select p
        from Post p
        where p.isDeleted = false
          and (
              p.visibility = 'PUBLIC'
              or (
                  p.visibility = 'FRIENDS'
                  and (
                      p.authorId = :viewerId
                      or exists (
                          select 1
                          from Friend f
                          where (f.user1Id = p.authorId and f.user2Id = :viewerId)
                             or (f.user1Id = :viewerId and f.user2Id = p.authorId)
                      )
                  )
              )
          )
        order by p.createdAt desc, p.id desc
    """)
    Page<Post> findVisibleFeedPosts(@Param("viewerId") Long viewerId, Pageable pageable);
}
