package com.devacademy.DevAcademy_BE.mapper;

import com.devacademy.DevAcademy_BE.dto.submitDTO.SubmissionResponseDTO;
import com.devacademy.DevAcademy_BE.entity.SubmissionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {
    SubmissionResponseDTO toSubmissionResponseDTO(SubmissionEntity submissionEntity);
}
