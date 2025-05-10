package com.devacademy.DevAcademy_BE.dto;

import com.devacademy.DevAcademy_BE.enums.StudentCourseStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuyCourseResponseDTO {
    Long id;
    Long idCourse;
    UUID idUser;
    StudentCourseStatus status;
}
