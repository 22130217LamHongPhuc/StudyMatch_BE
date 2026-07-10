package com.example.microservice.service;

import com.example.microservice.entity.User;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.exception.AppException;
import com.example.microservice.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class GoogleAuthService {

    @Autowired
    UserRepository userRepository;

    @Value("${google.client-id}")
    private String googleClientId;

    public GoogleIdToken.Payload verifyIdToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier =
                    new GoogleIdTokenVerifier.Builder(
                            new NetHttpTransport(),
                            GsonFactory.getDefaultInstance()
                    )
                            .setAudience(Collections.singletonList(googleClientId))
                            .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                throw new AppException("Token Google không hợp lệ", StatusCode.INVALID_TOKEN);
            }

            return idToken.getPayload();

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("Xác thực token Google thất bại", StatusCode.INVALID_TOKEN);
        }
    }

    public User findOrCreateGoogleUser(String email, String name, String picture) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            return userOpt.get();
        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPasswordHash("");
            newUser.setFullName(name);
            newUser.setAvatarUrl(picture);
            newUser.setRole("student");
            newUser.setStatus("ACTIVE");
            newUser.setEmailVerified(true);
            newUser.setOnboardingCompleted(false);
            return userRepository.save(newUser);
        }
    }
}
