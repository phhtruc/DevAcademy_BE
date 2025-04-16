package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.promptDTO.PromptRequestDTO;
import com.devacademy.DevAcademy_BE.dto.promptDTO.PromptResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PromptService {

    PromptResponseDTO getByActive();

    PromptResponseDTO saveConfig(PromptRequestDTO config);

    PromptResponseDTO updateConfig(Long id, PromptRequestDTO config);

    PageResponse<?> getAllReviewConfig(int page, int pageSize, Long courseId);

    PromptResponseDTO getById(Long id);

    PromptResponseDTO updateConfigActive(Long id);

    void delete(Long id);

    List<PromptResponseDTO> getAllPromptByIdCourse(Long id);
}
