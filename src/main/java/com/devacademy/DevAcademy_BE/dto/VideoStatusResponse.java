package com.devacademy.DevAcademy_BE.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoStatusResponse {
    private Long lessonId;
    private String status;
    private String videoUrl;
    private String errorMessage;
    private Integer progress;
}
