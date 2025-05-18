package com.devacademy.DevAcademy_BE.dto.chapterDTO;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterResponseDTO {
    Long id;
    String name;
    Integer chapterOrder;
    Long courseId;
}
