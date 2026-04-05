package com.example.microservice.service;

import com.example.microservice.dto.LoginResponse;
import com.example.microservice.dto.RegisterRequest;
import com.example.microservice.dto.RegisterResponse;
import com.example.microservice.entity.StudentProfile;
import com.example.microservice.entity.User;
import com.example.microservice.entity.UserSubject;
import com.example.microservice.repository.StudentProfileRepository;
import com.example.microservice.repository.SubjectRepository;
import com.example.microservice.repository.UserRepository;
import com.example.microservice.repository.UserSubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserSubjectRepository userSubjectRepository;

    @Autowired
    private JWTService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public RegisterResponse registerUser(RegisterRequest request) {
        System.out.println("Registering user nè "+ request);
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setStatus("ACTIVE");
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        user.setCreatedAt(java.time.Instant.now());
        user.setUpdatedAt(java.time.Instant.now());

        User savedUser = userRepository.save(user);

        StudentProfile profile = new StudentProfile();
        profile.setUserId(savedUser.getId());
        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }
        if (request.getLearningStyle() != null) {
            profile.setLearningStyle(request.getLearningStyle());
        }
        if (request.getGpa() != null) {
            profile.setGpa(request.getGpa());
        }

        studentProfileRepository.save(profile);

        if (request.getSubjectIds() != null && !request.getSubjectIds().isEmpty()) {
            for (Integer subjectId : request.getSubjectIds()) {
                if (subjectRepository.existsById(subjectId)) {
                    UserSubject userSubject = new UserSubject();
                    userSubject.setUserId(savedUser.getId());
                    userSubject.setSubjectId(subjectId);
                    userSubjectRepository.save(userSubject);
                }
            }
        }

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getAvatarUrl()
        );
    }
    public LoginResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email hoặc mật khẩu không đúng"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Email hoặc mật khẩu không đúng");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());
        String token = jwtService.generateToken(claims, user.getEmail());
        return new LoginResponse(user.getFullName(), token, user.getEmail());
    }
}
