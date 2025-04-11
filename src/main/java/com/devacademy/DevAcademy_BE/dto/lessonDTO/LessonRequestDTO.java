package com.devacademy.DevAcademy_BE.dto.lessonDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonRequestDTO {
    @NotBlank(message = "LESSON_TITLE_INVALID")
    String name;
    @NotBlank(message = "LESSON_TYPE_INVALID")
    @Pattern(regexp = "READINGS|LECTURES|EXERCISES", message = "COURSE_TYPE_INVALID_TYPE")
    String type;
    @NotBlank(message = "LESSON_CONTENT_INVALID")
    String content;
    @NotNull(message = "LESSON_VIDEO_URL_INVALID")
    String videoUrl;
    @NotNull(message = "COURSE_CONTENT_REFER_INVALID")
    String contentRefer;
    @NotBlank(message = "CHAPTER_ID_INVALID")
    String chapterId;
}
