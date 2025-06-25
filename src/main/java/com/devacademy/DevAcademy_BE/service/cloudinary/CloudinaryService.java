package com.devacademy.DevAcademy_BE.service.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("secure", true)
        );
        return (String) uploadResult.get("url");
    }

    public String uploadImage(File file, String originalFilename, String contentType) throws IOException {

        Map<String, String> params = new HashMap<>();
        params.put("public_id", UUID.randomUUID().toString());
        params.put("resource_type", "auto");

        Map<?, ?> uploadResult = cloudinary.uploader().upload(file, params);

        return uploadResult.get("url").toString();
    }

    public String uploadLargeVideo(MultipartFile multipartFile) throws IOException {
        File tempFile = File.createTempFile("upload-", multipartFile.getOriginalFilename());
        multipartFile.transferTo(tempFile);

        Map<String, Object> options = ObjectUtils.asMap(
                "resource_type", "video",
                "folder", "videos",
                "use_filename", true,
                "unique_filename", true,
                "overwrite", false,
                "secure", true
        );

        Map uploadResult = cloudinary.uploader().upload(tempFile, options);

        if (!tempFile.delete()) {
            log.warn("Không thể xóa file tạm: {}", tempFile.getAbsolutePath());
        }

        return (String) uploadResult.get("url");
    }

    public String uploadLargeVideo(File videoFile) throws IOException {
        Map<String, Object> options = ObjectUtils.asMap(
                "resource_type", "video",
                "folder", "videos",
                "use_filename", true,
                "unique_filename", true,
                "overwrite", false,
                "secure", true
        );

        Map uploadResult = cloudinary.uploader().upload(videoFile, options);

        return (String) uploadResult.get("url");
    }
}


