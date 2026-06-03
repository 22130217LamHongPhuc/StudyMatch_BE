package com.example.microservice.service;

import com.example.microservice.dto.respone.*;
import com.example.microservice.dto.request.UpdateUserProfileRequest;
import com.example.microservice.entity.User;
import com.example.microservice.feignAPI.SocialClient;
import com.example.microservice.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    UserRepository repo;
    @Autowired
    SocialClient socialClient;

    public User getProfile (Long userId ){
        return repo.findUsersByUserId(userId);
    }
    public Long getFriendCount(Long userId) {
        return socialClient.getTotalFriends(userId);
    }
    public MutualFriendsDto getMutualFriends(Long id, Long targetId){
        MutualFriendsDto res= socialClient.getMutualFriends(id, targetId);
        System.out.println(res + "nhận về êf");
        return res;
    }
    public ProfileDto getProfile(Long id, Long targetId){
        Long friendsCount = getFriendCount(targetId);
        MutualFriendsDto mutualDto = getMutualFriends(id, targetId);
        User user = getProfile(targetId);
        ProfileDto  res = new ProfileDto();
        res.setAvatarUrl(user.getAvatarUrl());
        res.setStatusFriend(mutualDto.getStatusFriend());
        res.setBio(user.getBio());
        res.setFullName(user.getFullName());
        res.setMutualFriend(mutualDto.getMutualFriends());
        res.setFriend(mutualDto.isFriend());
        res.setNumberFriend(friendsCount);
        System.out.println(res.toString() + "profile nè");
        return res;
    }

    public ProfileDto updateProfile(Long userId, UpdateUserProfileRequest request) {
        User user = repo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (request.getFullName() != null) {
            user.setFullName(normalizeNullable(request.getFullName()));
        }
        if (request.getBio() != null) {
            user.setBio(normalizeNullable(request.getBio()));
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(normalizeNullable(request.getAvatarUrl()));
        }
        User saved = repo.save(user);

        ProfileDto res = new ProfileDto();
        res.setAvatarUrl(saved.getAvatarUrl());
        res.setBio(saved.getBio());
        res.setFullName(saved.getFullName());
        res.setMutualFriend(0L);
        res.setNumberFriend(getFriendCount(userId));
        res.setFriend(true);
        return res;
    }

    private String normalizeNullable(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }



    public PageResponse<AdminUserListItemResponse> getUsersForAdmin(int page,int size,String keyword,String status,String role) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<User> userPage = repo.findUsersForAdmin(keyword,status,role,pageable);
        List<AdminUserListItemResponse> items = userPage.getContent().stream()
                .map(AdminUserListItemResponse::from).toList();


        return PageResponse.<AdminUserListItemResponse>builder()
                .content(items)
                .page(page)
                .limit(size)
                .totalPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .hasNext(userPage.hasNext())
                .hasPrevious(userPage.hasPrevious())
                .build();
    }

    public List<BasicUserResponse> getBasicUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return List.of();
        List<User> users = repo.findAllByUserIdIn(userIds);
        return users.stream().map(BasicUserResponse::from).collect(Collectors.toList());
    }


    public AdminUserStatusResponse updateStatusUser(Long userId, @NotNull() String status) {
        User user = repo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(status);
        repo.save(user);
        return new AdminUserStatusResponse( user.getStatus());
    }
}
