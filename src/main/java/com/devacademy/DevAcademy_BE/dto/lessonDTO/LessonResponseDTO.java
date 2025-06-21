package com.devacademy.DevAcademy_BE.dto.lessonDTO;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonResponseDTO {
    String id;
    String name;
    String type;
    String lessonOrder;
    String content;
    String videoUrl;
    String contentRefer;
    String chapter;
    Boolean isPublic;
    Boolean isCompleted;
}
