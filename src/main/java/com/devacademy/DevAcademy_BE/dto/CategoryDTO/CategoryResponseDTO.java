package com.devacademy.DevAcademy_BE.dto.CategoryDTO;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponseDTO {
    Long id;
    String name;
}
