package com.devacademy.DevAcademy_BE.dto.techStackDTO;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TechStackResponseDTO {
    Long id;
    String name;
}
