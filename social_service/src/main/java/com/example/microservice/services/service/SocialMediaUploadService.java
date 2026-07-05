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
            String originalName = file.getOriginalFilename();
            String publicId = "file_" + System.currentTimeMillis();
            if (originalName != null && originalName.contains(".")) {
                int dotIndex = originalName.lastIndexOf('.');
                String nameWithoutExt = originalName.substring(0, dotIndex).replaceAll("[^a-zA-Z0-9-_]", "_");
                String ext = originalName.substring(dotIndex);
                String lowerExt = ext.toLowerCase();
                boolean isRaw = !java.util.List.of(".pdf", ".png", ".jpg", ".jpeg", ".gif", ".mp4", ".mov", ".avi", ".webm").contains(lowerExt);
                if (isRaw) {
                    publicId = nameWithoutExt + "_" + System.currentTimeMillis() + ext;
                } else {
                    publicId = nameWithoutExt + "_" + System.currentTimeMillis();
                }
            } else if (originalName != null) {
                publicId = originalName.replaceAll("[^a-zA-Z0-9-_]", "_") + "_" + System.currentTimeMillis();
            }

            return cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "social_posts",
                            "resource_type", "auto",
                            "public_id", publicId
                    )
            );
        } catch (Exception ex) {
            throw new RuntimeException("Upload post media failed: " + ex.getMessage());
        }
    }
}
