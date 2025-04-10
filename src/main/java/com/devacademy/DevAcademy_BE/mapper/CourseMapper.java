package com.devacademy.DevAcademy_BE.mapper;

import com.devacademy.DevAcademy_BE.dto.courseDTO.CourseRequestDTO;
import com.devacademy.DevAcademy_BE.dto.courseDTO.CourseResponseDTO;
import com.devacademy.DevAcademy_BE.dto.techStackDTO.TechStackResponseDTO;
import com.devacademy.DevAcademy_BE.entity.CourseEntity;
import com.devacademy.DevAcademy_BE.entity.TechStackEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "techStacks", expression = "java(mapTechStacks(techStackEntities))")
    CourseResponseDTO toCourseResponseDTO(CourseEntity courseEntity, List<TechStackEntity> techStackEntities);

    default List<TechStackResponseDTO> mapTechStacks(List<TechStackEntity> techStackEntities) {
        if (techStackEntities == null) return Collections.emptyList();

        return techStackEntities.stream()
                .map(tech -> TechStackResponseDTO.builder()
                        .id(tech.getId())
                        .name(tech.getName())
                        .build())
                .toList();
    }

    CourseEntity toCourseEntity(CourseRequestDTO courseRequestDTO);

}
