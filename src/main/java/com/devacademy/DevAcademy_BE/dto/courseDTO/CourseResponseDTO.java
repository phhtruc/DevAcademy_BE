package com.devacademy.DevAcademy_BE.dto.courseDTO;

import com.devacademy.DevAcademy_BE.dto.techStackDTO.TechStackResponseDTO;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
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
}
