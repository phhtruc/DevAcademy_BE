package com.devacademy.DevAcademy_BE.dto.emailDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TrialRequestDTO {
    private String toEmail;
    private String courseName;
    private String userName;
    private String subject;
    private String title;
    private String description;
}
