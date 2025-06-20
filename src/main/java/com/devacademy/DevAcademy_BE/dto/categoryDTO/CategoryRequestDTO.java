package com.devacademy.DevAcademy_BE.dto.categoryDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRequestDTO {
    @NotBlank(message = "CATEGORY_NAME_INVALID")
    String name;
}
