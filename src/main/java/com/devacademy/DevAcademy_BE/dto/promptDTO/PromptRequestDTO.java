package com.devacademy.DevAcademy_BE.dto.promptDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromptRequestDTO {
    @NotBlank(message = "PROMPT_STRUCTURE")
    String contentStruct;
    @NotBlank(message = "PROMPT_STRUCTURE")
    String idCourse;
}
