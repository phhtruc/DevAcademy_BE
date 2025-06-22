package com.devacademy.DevAcademy_BE.dto.studentDTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentCourseDetailsDTO {
    private Long id;
    private String name;
    private String thumbnailUrl;
    private int progress;
    private long completedLessons;
    private long totalLessons;
    private long completedLectures;
    private long totalLectures;
    private long completedReadings;
    private long totalReadings;
    private long completedExercises;
    private long totalExercises;
    private LocalDateTime lastAccessedDate;
}
