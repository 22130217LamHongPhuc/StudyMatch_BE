package com.example.microservice.services.repository;

import com.example.microservice.services.entity.FriendRequest;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRequestRepo extends JpaRepository< FriendRequest, Long> {

    @Query(value = """
    select status
    from friend_requests
    where (sender_id = :id and receiver_id = :targetId) or (sender_id = :targetId and receiver_id = :id)

""", nativeQuery = true)
    public String statusFriends( @Param("id") long id, @Param("targetId") Long targetId);

}


