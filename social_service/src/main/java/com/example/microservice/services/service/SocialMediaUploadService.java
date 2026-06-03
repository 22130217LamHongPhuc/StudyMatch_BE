package com.example.microservice.services.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class SocialMediaUploadService {
    private final Cloudinary cloudinary;

    public SocialMediaUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Map uploadPostMedia(MultipartFile file) {
        try {
            return cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "social_posts",
                            "resource_type", "auto"
                    )
            );
        } catch (Exception ex) {
            throw new RuntimeException("Upload post media failed: " + ex.getMessage());
        }
    }
}
