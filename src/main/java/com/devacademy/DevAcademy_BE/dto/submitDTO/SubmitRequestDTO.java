package com.devacademy.DevAcademy_BE.dto.submitDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitRequestDTO {
    @NotBlank(message = "GITHUB_LINK")
    String githubLink;
    String exerciseTitle;
    String idExercise;
}
