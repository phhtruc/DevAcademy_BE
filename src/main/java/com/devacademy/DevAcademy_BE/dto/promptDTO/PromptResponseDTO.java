package com.devacademy.DevAcademy_BE.dto.promptDTO;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromptResponseDTO implements Serializable {
    Long id;
    String contentStruct;
    Boolean isActive;
    Long idCourse;
}
