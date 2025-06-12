package com.devacademy.DevAcademy_BE.dto.submitDTO;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubmissionResponseDTO {
    String review;
    LocalDateTime createdDate;
}
