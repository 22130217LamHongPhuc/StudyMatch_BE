package com.example.microservice.service;

import com.example.microservice.annotation.AuditLog;
import com.example.microservice.dto.respone.*;
import com.example.microservice.dto.request.UpdateUserProfileRequest;
import com.example.microservice.entity.User;
import com.example.microservice.enums.StatusCode;
import java.util.Map;
import java.util.HashMap;
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
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    UserRepository repo;
    @Autowired
    SocialClient socialClient;
    @Autowired
    ProfileClient profileClient;
    @Autowired
    com.example.microservice.feignAPI.ChatClient chatClient;

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



    public PageResponse<AdminUserListItemResponse> getUsersForAdmin(int page, int size, String keyword, String status, String role, Long excludeUserId) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<User> userPage = repo.findUsersForAdmin(keyword, status, role, excludeUserId, pageable);
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
                "ACTIVE", "student", null, pageable);
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


    @AuditLog(action = "UPDATE_STUDENT_STATUS", targetType = "USER", targetId = "#userId", details = "'Cập nhật trạng thái người dùng thành: ' + #status")
    public AdminUserStatusResponse updateStatusUser(Long userId, @NotNull() String status) {
        User user = repo.findById(userId)
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng", StatusCode.USER_NOT_FOUND));
        user.setStatus(status);
        repo.save(user);

        if ("DELETED".equalsIgnoreCase(status) ||
            "LOCKED".equalsIgnoreCase(status)) {
            try {
                Map<String, Object> reqBody = new HashMap<>();
                reqBody.put("userId", userId);
                reqBody.put("reason", "Tài khoản của bạn đã bị khóa hoặc ngừng hoạt động bởi quản trị viên.");
                chatClient.notifyForceLogout(reqBody);
            } catch (Exception e) {
                System.err.println("Failed to send force logout notification via chat-service: " + e.getMessage());
            }
        }

        return new AdminUserStatusResponse( user.getStatus());
    }

    @AuditLog(action = "UPDATE_ADMIN_STATUS", targetType = "ADMIN", targetId = "#targetAdminId", details = "'Cập nhật trạng thái thành: ' + #status")
    public AdminUserStatusResponse updateAdminStatus(Long targetAdminId, @NotNull() String status) {
        if (!"ACTIVE".equalsIgnoreCase(status) && !"LOCKED".equalsIgnoreCase(status)) {
            throw new AppException("Trạng thái không hợp lệ", StatusCode.ACCESS_DENIED);
        }

        User user = repo.findById(targetAdminId)
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng", StatusCode.USER_NOT_FOUND));

        if (!"admin".equalsIgnoreCase(user.getRole())) {
            throw new AppException("Tài khoản không phải là quản trị viên", StatusCode.ACCESS_DENIED);
        }

        user.setStatus(status.toUpperCase());
        repo.save(user);

        if ("LOCKED".equalsIgnoreCase(status)) {
            try {
                Map<String, Object> reqBody = new HashMap<>();
                reqBody.put("userId", targetAdminId);
                reqBody.put("reason", "Tài khoản Admin của bạn đã bị khóa bởi Super Admin.");
                chatClient.notifyForceLogout(reqBody);
            } catch (Exception e) {
                System.err.println(
                        "Failed to send force logout notification for admin via chat-service: " + e.getMessage());
            }
        }

        return new AdminUserStatusResponse(user.getStatus());
    }

    @AuditLog(action = "UPDATE_ADMIN_PROFILE", targetType = "ADMIN", targetId = "#userId", details = "'Cập nhật thông tin cá nhân'")
    public Map<String, Object> updateAdminProfile(Long userId, UpdateUserProfileRequest request) {
        User user = repo.findById(userId)
                .orElseThrow(() -> new AppException("Không tìm thấy người dùng", StatusCode.USER_NOT_FOUND));

        if (!"admin".equalsIgnoreCase(user.getRole()) && !"super_admin".equalsIgnoreCase(user.getRole())) {
            throw new AppException("Tài khoản không phải là quản trị viên", StatusCode.ACCESS_DENIED);
        }

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

        Map<String, Object> response = new HashMap<>();
        response.put("userId", saved.getUserId());
        response.put("fullName", saved.getFullName());
        response.put("avatarUrl", saved.getAvatarUrl());
        response.put("email", saved.getEmail());
        response.put("bio", saved.getBio());
        response.put("role", saved.getRole());
        return response;
    }
}
