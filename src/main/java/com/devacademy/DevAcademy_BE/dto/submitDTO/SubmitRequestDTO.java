package com.devacademy.DevAcademy_BE.dto.submitDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitRequestDTO {
    String githubLink;
    String exerciseTitle;
    String idExercise;
    String idCourse;
    String language = "vietnamese";
}
