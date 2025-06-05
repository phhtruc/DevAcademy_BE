package com.devacademy.DevAcademy_BE.dto.courseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseSearchDTO {
    private String name;
    private String type;
    private String categoryId;
}
