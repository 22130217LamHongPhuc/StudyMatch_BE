package com.example.microservice.service;

import com.example.microservice.dto.respone.*;
import com.example.microservice.dto.request.UpdateUserProfileRequest;
import com.example.microservice.entity.User;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.exception.AppException;
import com.example.microservice.feignAPI.ProfileClient;
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
    @Autowired
    ProfileClient profileClient;

    public User getProfile (Long userId ){
        return repo.findUsersByUserId(userId);
    }

    public String getFullName(Long userId) {
        User user = repo.findById(userId)
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng", StatusCode.USER_NOT_FOUND));
        return user.getFullName();
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
        User user = repo.findById(userId)
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng", StatusCode.USER_NOT_FOUND));
        
        boolean isNameUpdated = false;
        boolean isAvatarUpdated = false;

        if (request.getFullName() != null) {
            String newFullName = normalizeNullable(request.getFullName());
            if (!java.util.Objects.equals(user.getFullName(), newFullName)) {
                user.setFullName(newFullName);
                isNameUpdated = true;
            }
        }
        if (request.getBio() != null) {
            user.setBio(normalizeNullable(request.getBio()));
        }
        if (request.getAvatarUrl() != null) {
            String newAvatarUrl = normalizeNullable(request.getAvatarUrl());
            if (!java.util.Objects.equals(user.getAvatarUrl(), newAvatarUrl)) {
                user.setAvatarUrl(newAvatarUrl);
                isAvatarUpdated = true;
            }
        }

        User saved = repo.save(user);

        if (isNameUpdated || isAvatarUpdated) {
            try {
                profileClient.updateStudentProfileInfo(userId, saved.getFullName(), saved.getAvatarUrl());
            } catch (Exception e) {
                System.err.println("Failed to update student profile in profile service: " + e.getMessage());
            }
        }

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

    public PageResponse<StudentSearchResponse> searchStudents(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "fullName")
        );
        Page<User> userPage = repo.findUsersForAdmin(
                keyword != null ? keyword.toLowerCase() : null,
                "ACTIVE", "student", pageable);
        List<StudentSearchResponse> items = userPage.getContent().stream()
                .map(StudentSearchResponse::from)
                .toList();

        return PageResponse.<StudentSearchResponse>builder()
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
        User user = repo.findById(userId)
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng", StatusCode.USER_NOT_FOUND));
        user.setStatus(status);
        repo.save(user);
        return new AdminUserStatusResponse( user.getStatus());
    }
}
