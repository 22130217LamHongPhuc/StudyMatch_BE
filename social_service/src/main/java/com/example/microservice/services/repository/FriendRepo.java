package com.example.microservice.services.repository;

import com.example.microservice.services.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepo extends JpaRepository<Friend, Long> {

    @Query(value = """
select count(*)
from friends
where (user1_id = :userId or user2_id = :userId)
  and user1_id <> user2_id
""", nativeQuery = true)
    Long countTotalFriend(@Param("userId") Long userId);

    @Query(value = """
SELECT COUNT(*)
FROM friends f1
JOIN friends f2
  ON (
    CASE
      WHEN f1.user1_id = :u1 THEN f1.user2_id
      ELSE f1.user1_id
    END
  ) = (
    CASE
      WHEN f2.user1_id = :u2 THEN f2.user2_id
      ELSE f2.user1_id
    END
  )
WHERE (f1.user1_id = :u1 OR f1.user2_id = :u1)
  AND (f2.user1_id = :u2 OR f2.user2_id = :u2)
  AND f1.user1_id <> f1.user2_id
  AND f2.user1_id <> f2.user2_id
""", nativeQuery = true)
    Long countMutualFriend(@Param("u1") Long u1, @Param("u2") Long u2);

    @Query(value = """
select count(*)
from friends
where user1_id <> user2_id
  and ((user1_id = :user1 and user2_id = :user2) or (user1_id = :user2 and user2_id = :user1))
""", nativeQuery = true)
    Long isFriends(@Param("user1") Long user1, @Param("user2") Long user2);

    @Query(value = """
select
  case
    when user1_id = :userId then user2_id
    else user1_id
  end
from friends
where (user1_id = :userId or user2_id = :userId)
  and user1_id <> user2_id
order by created_at desc
""", nativeQuery = true)
    List<Long> findFriendUserIds(@Param("userId") Long userId);
}
