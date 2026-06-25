package com.example.microservice.services.service;

import com.example.microservice.services.entity.FriendRequest;
import com.example.microservice.services.repository.FriendRequestRepo;
import com.example.microservice.services.Dto.FriendRequestDto;
import com.example.microservice.services.Dto.AllFriendRequestsDto;
import com.example.microservice.services.entity.Friend;
import com.example.microservice.services.repository.FriendRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendRequestService {
    @Autowired
    FriendRequestRepo repo;
    @Autowired
    FriendRepo friendRepo;


    public FriendRequest friendRequest(Long senderId,Long reveiverId){
        FriendRequest req = new FriendRequest();
        req.setSenderId(senderId);
        req.setStatus("PENDING");
        req.setReceiverId(reveiverId);
        req.setCreatedAt(getTimeZone());
        req.setUpdatedAt(getTimeZone());
       return repo.save(req);
    }

    public LocalDateTime getTimeZone(){
        Instant instant = Instant.now();
      return LocalDateTime.ofInstant(
                instant,
                ZoneId.of("Asia/Ho_Chi_Minh")
        );
    }

    public String statusFriend(Long id, Long targetId){
        return repo.statusFriends(id, targetId);
    }

    private FriendRequestDto toDto(FriendRequest req){
        FriendRequestDto dto = new FriendRequestDto();
        dto.setId(req.getId());
        dto.setSenderId(req.getSenderId());
        dto.setReceiverId(req.getReceiverId());
        dto.setStatus(req.getStatus());
        dto.setCreatedAt(req.getCreatedAt());
        dto.setUpdatedAt(req.getUpdatedAt());
        return dto;
    }

    public List<FriendRequestDto> getSentRequests(Long userId){
        List<FriendRequest> list = repo.findBySenderIdOrderByUpdatedAtDesc(userId);
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<FriendRequestDto> getReceivedRequests(Long userId){
        List<FriendRequest> list = repo.findByReceiverIdOrderByUpdatedAtDesc(userId);
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<FriendRequestDto> getSentRequests(Long userId, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        List<FriendRequest> list = repo.findBySenderIdOrderByUpdatedAtDesc(userId, pageable);
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<FriendRequestDto> getReceivedRequests(Long userId, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        List<FriendRequest> list = repo.findByReceiverIdOrderByUpdatedAtDesc(userId, pageable);
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    public AllFriendRequestsDto getAllRequests(Long userId){
        AllFriendRequestsDto res = new AllFriendRequestsDto();
        res.setSent(getSentRequests(userId));
        res.setReceived(getReceivedRequests(userId));
        return res;
    }

    public AllFriendRequestsDto getAllRequests(Long userId, Integer page, Integer size){
        AllFriendRequestsDto res = new AllFriendRequestsDto();
        if (page != null && size != null){
            res.setSent(getSentRequests(userId, page, size));
            res.setReceived(getReceivedRequests(userId, page, size));
        } else {
            res.setSent(getSentRequests(userId));
            res.setReceived(getReceivedRequests(userId));
        }
        return res;
    }

    @Transactional
    public FriendRequestDto updateStatus(Long requestId, String status){
        FriendRequest req = repo.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend request not found"));

        req.setStatus(status.toUpperCase());
        req.setUpdatedAt(getTimeZone());
        FriendRequest saved = repo.save(req);

        if ("APPROVED".equalsIgnoreCase(status)) {
            Long friendCount = friendRepo.isFriends(req.getSenderId(), req.getReceiverId());
            if (friendCount == null || friendCount == 0) {
                Friend friend = new Friend();
                friend.setUser1Id(req.getSenderId());
                friend.setUser2Id(req.getReceiverId());
                friend.setCreatedAt(Instant.now());
                friendRepo.save(friend);
            }
        }

        return toDto(saved);
    }

    @Transactional
    public FriendRequestDto updateStatusBySenderAndReceiver(Long senderId, Long receiverId, String status){
        FriendRequest req = repo.findBySenderIdAndReceiverId(senderId, receiverId);
        if (req == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friend request not found");
        }

        req.setStatus(status.toUpperCase());
        req.setUpdatedAt(getTimeZone());
        FriendRequest saved = repo.save(req);

        if ("APPROVED".equalsIgnoreCase(status)) {
            Long friendCount = friendRepo.isFriends(req.getSenderId(), req.getReceiverId());
            if (friendCount == null || friendCount == 0) {
                Friend friend = new Friend();
                friend.setUser1Id(req.getSenderId());
                friend.setUser2Id(req.getReceiverId());
                friend.setCreatedAt(Instant.now());
                friendRepo.save(friend);
            }
        }

        return toDto(saved);
    }

}
