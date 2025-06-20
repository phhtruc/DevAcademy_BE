package com.devacademy.DevAcademy_BE.dto.techStackDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TechStackRequestDTO {
    @NotBlank(message = "TECH_STACK_NAME_INVALID")
    String name;
}
