package com.devacademy.DevAcademy_BE.mapper;

import com.devacademy.DevAcademy_BE.dto.lessonDTO.LessonRequestDTO;
import com.devacademy.DevAcademy_BE.dto.lessonDTO.LessonResponseDTO;
import com.devacademy.DevAcademy_BE.entity.LessonEntity;
import com.devacademy.DevAcademy_BE.repository.ChapterRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ChapterRepository.class)
public interface LessonMapper {
    @Mapping(source = "chapterEntity.id", target = "chapter")
    LessonResponseDTO toLessonResponseDTO(LessonEntity lesson);

    @Mapping(source = "chapterId", target = "chapterEntity.id")
    LessonEntity toLessonEntity(LessonRequestDTO lesson);
}
