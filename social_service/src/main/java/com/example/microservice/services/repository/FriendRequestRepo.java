package com.example.microservice.services.repository;

import com.example.microservice.services.entity.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRequestRepo extends JpaRepository< FriendRequest, Long> {
}
