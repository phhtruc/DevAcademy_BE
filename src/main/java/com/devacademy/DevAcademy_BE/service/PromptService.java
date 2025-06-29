package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.promptDTO.PromptRequestDTO;
import com.devacademy.DevAcademy_BE.dto.promptDTO.PromptResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface PromptService {

    PromptResponseDTO saveConfig(PromptRequestDTO config);

    PromptResponseDTO updateConfig(Long id, PromptRequestDTO config);

    PageResponse<?> getAllReviewConfig(int page, int pageSize, Long courseId);

    PromptResponseDTO getById(Long id);

    PromptResponseDTO updateConfigActive(Long id, Long idCourse);

    void delete(Long id);
}
