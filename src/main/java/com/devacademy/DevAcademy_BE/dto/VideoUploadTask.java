package com.devacademy.DevAcademy_BE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoUploadTask {
    private MultipartFile video;
    private Long lessonId;
}
