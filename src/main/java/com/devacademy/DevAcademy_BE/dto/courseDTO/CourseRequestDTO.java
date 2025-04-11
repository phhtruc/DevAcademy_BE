package com.devacademy.DevAcademy_BE.dto.courseDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseRequestDTO {
    @NotBlank(message = "COURSE_NAME_INVALID")
    String name;
    @NotBlank(message = "COURSE_PRICE_INVALID")
    String price;
    @NotBlank(message = "COURSE_DESCRIPTION_INVALID")
    String description;
    @NotBlank(message = "COURSE_IS_PUBLIC_INVALID")
    @Pattern(regexp = "true|false", message = "COURSE_IS_PUBLIC_INVALID_TYPE")
    String isPublic;
    @NotNull(message = "COURSE_TECH_STACK_INVALID")
    List<String> techStack;
}
