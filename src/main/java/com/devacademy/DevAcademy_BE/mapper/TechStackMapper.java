package com.devacademy.DevAcademy_BE.mapper;

import com.devacademy.DevAcademy_BE.dto.techStackDTO.TechStackRequestDTO;
import com.devacademy.DevAcademy_BE.dto.techStackDTO.TechStackResponseDTO;
import com.devacademy.DevAcademy_BE.entity.TechStackEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TechStackMapper {

    TechStackResponseDTO toTechStackResponse(TechStackEntity techStack);

    TechStackEntity toTechStackEntity(TechStackRequestDTO requestDTO);
}
