package com.devacademy.DevAcademy_BE.dto.chapterDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterRequestDTO {

    @NotBlank(message = "CHAPTER_NAME_INVALID")
    String name;
    @NotBlank(message = "CHAPTER_IS_PUBLIC_INVALID")
    String isPublic;
    @NotBlank(message = "CHAPTER_COURSE_ID_INVALID")
    String courseId;

}
