package com.example.microservice.services.repository;

import com.example.microservice.services.entity.FriendRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRequestRepo extends JpaRepository< FriendRequest, Long> {

    @Query(value = """
    select status
    from friend_requests
    where (sender_id = :id and receiver_id = :targetId) or (sender_id = :targetId and receiver_id = :id)
    order by updated_at desc, created_at desc, id desc
    limit 1

""", nativeQuery = true)
    public String statusFriends( @Param("id") long id, @Param("targetId") Long targetId);

    List<FriendRequest> findBySenderIdOrderByUpdatedAtDesc(Long senderId);

    List<FriendRequest> findByReceiverIdOrderByUpdatedAtDesc(Long receiverId);

    List<FriendRequest> findBySenderIdOrderByUpdatedAtDesc(Long senderId, Pageable pageable);
    List<FriendRequest> findByReceiverIdOrderByUpdatedAtDesc(Long receiverId, Pageable pageable);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query(value = """
    delete from friend_requests
    where (sender_id = :u1 and receiver_id = :u2) or (sender_id = :u2 and receiver_id = :u1)
    """, nativeQuery = true)
    void deleteFriendRequests(@Param("u1") Long u1, @Param("u2") Long u2);

}

