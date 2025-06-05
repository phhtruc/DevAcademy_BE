package com.devacademy.DevAcademy_BE.dto.courseDTO;

import com.devacademy.DevAcademy_BE.dto.categoryDTO.CategoryResponseDTO;
import com.devacademy.DevAcademy_BE.dto.techStackDTO.TechStackResponseDTO;
import com.devacademy.DevAcademy_BE.enums.RegisterType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseResponseDTO {
    Long id;
    String name;
    BigDecimal price;
    String thumbnailUrl;
    String description;
    Boolean isPublic;
    List<TechStackResponseDTO> techStacks;
    CategoryResponseDTO category;
    String duration;
    Integer lessonCount;
    Boolean isPurchased;
    LocalDateTime modifiedDate;
    Integer totalRegister;
}
