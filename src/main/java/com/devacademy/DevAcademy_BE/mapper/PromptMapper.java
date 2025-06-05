package com.devacademy.DevAcademy_BE.mapper;

import com.devacademy.DevAcademy_BE.dto.promptDTO.PromptRequestDTO;
import com.devacademy.DevAcademy_BE.dto.promptDTO.PromptResponseDTO;
import com.devacademy.DevAcademy_BE.entity.PromptEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PromptMapper {

    @Mapping(source = "idCourse", target = "courseEntity.id")
    PromptEntity toPromptEntity(PromptRequestDTO requestDTO);

    @Mapping(source = "courseEntity.id", target = "idCourse")
    PromptResponseDTO toPromptConfigResponseDTO(PromptEntity entity);
}
