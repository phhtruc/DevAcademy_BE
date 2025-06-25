package com.devacademy.DevAcademy_BE.dto;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Getter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class VideoUploadTask {
    byte[] videoContent;
    String originalFilename;
    Long lessonId;

    public VideoUploadTask(MultipartFile file, Long lessonId) throws IOException {
        this.videoContent = file.getBytes();
        this.originalFilename = file.getOriginalFilename();
        this.lessonId = lessonId;
    }
}
