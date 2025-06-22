package com.devacademy.DevAcademy_BE.dto.studentDTO;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDTO {
    private UUID id;
    private String name;
    private String email;
    private String avatar;
    private LocalDateTime lastActivityDate;
}
