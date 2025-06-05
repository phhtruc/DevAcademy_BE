package com.devacademy.DevAcademy_BE.mapper;

import com.devacademy.DevAcademy_BE.dto.chapterDTO.ChapterRequestDTO;
import com.devacademy.DevAcademy_BE.dto.chapterDTO.ChapterResponseDTO;
import com.devacademy.DevAcademy_BE.entity.ChapterEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChapterMapper {

    @Mapping(source = "courseId", target = "courseEntity.id")
    ChapterEntity toChapterEntity(ChapterRequestDTO chapterRequestDTO);

    @Mapping(source = "courseEntity.id", target = "courseId")
    ChapterResponseDTO toChapterResponseDTO(ChapterEntity chapterEntity);

}
